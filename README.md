<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
Overview 
======================
This is a hopefully short-lived fork of the [Apache Maven GPG Plugin](https://github.com/apache/maven-gpg-plugin), meant to work around an issue some WildFly ecosystem builds see when using `maven-gpg-plugin` in conjunction with Sonatype's [`nxrm3-maven-plugin`](https://github.com/sonatype/nxrm3-maven-plugin). It exists solely to work around that issue and allow affected WildFly projects to perform staged releases to a Nexus 3 repository. As soon as some other solution is found this project will be archived.

See also:

* A [possible `nxrm3-maven-plugin` fix](https://github.com/sonatype/nxrm3-maven-plugin/pull/45)
* A [possible Apache Maven GPG Plugin fix](https://github.com/apache/maven-gpg-plugin/pull/135)

Releasing
======================

Use the `apache-release` profile:

`mvn clean deploy -Prun-its -Papache-release`

[license]: https://www.apache.org/licenses/LICENSE-2.0
[ml-list]: https://maven.apache.org/mailing-lists.html
[code-style]: https://maven.apache.org/developers/conventions/code.html
[cla]: https://www.apache.org/licenses/#clas
[maven-wiki]: https://cwiki.apache.org/confluence/display/MAVEN/Index
[test-results]: https://ci-maven.apache.org/job/Maven/job/maven-box/job/maven-gpg-plugin/job/master/lastCompletedBuild/testReport/
[build]: https://ci-maven.apache.org/job/Maven/job/maven-box/job/maven-gpg-plugin/job/master/
