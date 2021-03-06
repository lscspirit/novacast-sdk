# Swagger Codegen for the Novacast SDK library

## Overview
This is a boiler-plate project to generate your own client library with Swagger.  It's goal is
to get you started with the basic plumbing so you can put in your own logic.  It won't work without
your changes applied.

## What's Swagger?
The goal of Swagger™ is to define a standard, language-agnostic interface to REST APIs which allows both humans and computers to discover and understand the capabilities of the service without access to source code, documentation, or through network traffic inspection. When properly defined via Swagger, a consumer can understand and interact with the remote service with a minimal amount of implementation logic. Similar to what interfaces have done for lower-level programming, Swagger removes the guesswork in calling the service.


Check out [Swagger-Spec](https://github.com/swagger-api/swagger-spec) for additional information about the Swagger project, including additional libraries with support for other languages and more.

## How do I use this?
At this point, you've likely generated a client setup.  It will include something along these lines:

```
.
|- README.md    // this file
|- pom.xml      // build script
|-- src
|--- main
|---- java
|----- com.novacast.SdkRubyGenerator.java // generator file
|----- com.novacast.SdkJsGenerator.java // generator file
|---- resources
|----- sdkRuby // template files
|----- sdkJs // template files
|----- META-INF
|------ services
|------- io.swagger.codegen.CodegenConfig
```

You _will_ need to make changes in at least the following:

`SdkRubyGenerator.java`
`SdkJsGenerator.java`

Templates in this folder:

`src/main/resources/sdkRuby`
`src/main/resources/sdkJs`

Once modified, you can run this:

```
mvn package
```

Now your templates are available to the client generator.


A custom NodeJS script is available for use to generate the SDKs:

```
node scripts/generate_sdk.js -s apis/event_service/v1 -l sdkRuby -o output/sdkRuby
```

```
node scripts/generate_sdk.js -s apis/event_service/v1 -l sdkJs -o output/sdkJs
```

## But how do I modify this?
The `SdkRubyGenerator.java` and `SdkJsGenerator.java` have comments in it--lots of comments.  There is no good substitute
for reading the code more, though.  See how the `SdkRubyGenerator` and `SdkJsGenerator` implements `CodegenConfig`.
That class has the signature of all values that can be overridden.

For the templates themselves, you have a number of values available to you for generation.
You can execute the `java` command from above while passing different debug flags to show
the object you have available during client generation:

```
# The following additional debug options are available for all codegen targets:
# -DdebugSwagger prints the swagger specification as interpreted by the codegen
# -DdebugModels prints models passed to the template engine
# -DdebugOperations prints operations passed to the template engine
# -DdebugSupportingFiles prints additional data passed to the template engine

java -DdebugOperations -cp /path/to/swagger-codegen-distribution:/path/to/your/jar io.swagger.codegen.Codegen -l sdkRuby -o ./test
java -DdebugOperations -cp /path/to/swagger-codegen-distribution:/path/to/your/jar io.swagger.codegen.Codegen -l sdkJs -o ./test
```

Will, for example, output the debug info for operations.  You can use this info
in the `api.mustache` file.
