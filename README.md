# Template

Contains a template for a new Java project. By utilising the buildSrc Gradle pattern,
this template provides a fast start for plain Java projects as well as Spring Boot projects.

It also contains some modules which each show a different way to use the template:
- module-server: A Spring Boot server project
- module-plain: A plain Java project
- module-lib: A Java library that can be used in other modules

To use the template, simply copy the contents of this repository into a new repository.
Then, replace all occurrences of `template` and `Template` with the name of your project.
Finally, replace the contents of this README with the README of your project.

The settings.gradle file needs to be changed to import the modules that you want to use.
