/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.gpg;

import javax.inject.Inject;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
// import org.apache.maven.project.artifact.AttachedArtifact;

/**
 * Sign project artifact, the POM, and attached artifacts with GnuPG for deployment.
 *
 * @author Jason van Zyl
 * @author Jason Dillon
 * @author Daniel Kulp
 */
@Mojo(name = "sign", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class GpgSignAttachedMojo extends AbstractGpgMojo {

    /**
     * A list of files to exclude from being signed. Can contain Ant-style wildcards and double wildcards. The default
     * excludes are <code>**&#47;*.md5 **&#47;*.sha1 **&#47;*.sha256 **&#47;*.sha512 **&#47;*.asc **&#47;*.sigstore; **&#47;*.sigstore.json</code>.
     *
     * @since 1.0-alpha-4
     */
    @Parameter
    private String[] excludes;

    /**
     * The directory where to store signature files.
     *
     * @since 1.0-alpha-4
     */
    @Parameter(defaultValue = "${project.build.directory}/gpg", alias = "outputDirectory")
    private File ascDirectory;

    // @Component
    private final ArtifactHandlerManager artifactHandlerManager;

    /**
     * The maven project.
     */
    protected final MavenProject project;

    /**
     * Maven ProjectHelper
     */
    private final MavenProjectHelper projectHelper;

    @Inject
    public GpgSignAttachedMojo(
            MavenProject project, MavenProjectHelper projectHelper, ArtifactHandlerManager artifactHandlerManager) {
        this.project = project;
        this.projectHelper = projectHelper;
        this.artifactHandlerManager = artifactHandlerManager;
    }

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        // ----------------------------------------------------------------------------
        // Collect files to sign
        // ----------------------------------------------------------------------------

        FilesCollector collector = new FilesCollector(project, excludes, getLog());
        List<FilesCollector.Item> items = collector.collect();

        // ----------------------------------------------------------------------------
        // Sign collected files and attach all the signatures
        // ----------------------------------------------------------------------------

        AbstractGpgSigner signer = newSigner(project);
        signer.setOutputDirectory(ascDirectory);
        signer.setBuildDirectory(new File(project.getBuild().getDirectory()));
        signer.setBaseDirectory(project.getBasedir());

        getLog().info("Signer '" + signer.signerName() + "' is signing " + items.size() + " file"
                + ((items.size() > 1) ? "s" : "") + " with key " + signer.getKeyInfo());

        for (FilesCollector.Item item : items) {
            getLog().debug("Generating signature for " + item.getFile());

            File signature = signer.generateSignatureForArtifact(item.getFile());

            if (item.isSameGAV()) {
                // Typical case; use the logic from maven-gpg-plugin
                projectHelper.attachArtifact(
                        project,
                        item.getExtension() + AbstractGpgSigner.SIGNATURE_EXTENSION,
                        item.getClassifier(),
                        signature);
            } else {
                // Attach the signature file ourselves using the GAV data from the artifact whose file was signed
                attachArtifact(item, item.getExtension() + AbstractGpgSigner.SIGNATURE_EXTENSION, signature);
            }
        }
    }

    private void attachArtifact(FilesCollector.Item item, String artifactType, File artifactFile) {

        ArtifactHandler handler = artifactHandlerManager.getArtifactHandler(artifactType);

        final Artifact artifact = new DefaultArtifact(
                item.getGroupId(),
                item.getArtifactId(),
                item.getVersion(),
                item.getScope(),
                artifactType,
                item.getClassifier(),
                handler);
        artifact.setFile(artifactFile);
        artifact.setResolved(true);
        project.addAttachedArtifact(artifact);
    }
}
