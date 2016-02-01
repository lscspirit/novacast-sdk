# Novacast SDK

## Getting Started
### Download

Clone this repository:

    git clone https://github.com/lscspirit/novacast-sdk.git

Install all required Node packages:

    npm install

Build the custom Novacast codegen library:

    cd swagger-coden && mvn package

### APIs

Definitions for all APIs can be found under the ``apis`` folder (e.g. ``apis/event_service/v1``). APIs are defined using the [Swagger 2.0](http://swagger.io/specification/) specification.

### Generate SDK

To generate the actual Ruby or Javascript SDK for an API, use the ``scripts/generate_sdk.js`` script (see [Scripts](#scripts) section for more details). For example, to generate the Ruby SDK for EventService V1:

    node scripts/generate_sdk.js -s apis/event_service/v1 -l sdkRuby -o /tmp/sdkRuby

## API Definitions
Each API is defined within a directory of its own under the ``apis`` directory.

### Content
The API definition folder should contains the following:
- ``spec.yml`` - main api specification file
- ``config.json`` - additional configuration
- ``models`` - directory of model definitions
- ``paths`` - directory of operation definitions

#### spec.yml
The main specification file. This file must be in the [Swagger 2.0](http://swagger.io/specification/) format, with one exception - a custom ``$spec_ref`` field. By definition, Swagger 2.0 does not allow the specification to be split into multiple files. Therefore, a custom ``$spec_ref`` field is introduced to tell the ``compile_spec.js`` script to concatenate all definitions within the provided folder and inject the result into the main ``spec.yml``.

#### config.json
This file contains additional properties of the API:

- ``sdkName`` - name of the SDK
- ``moduleName`` - name of the module (use within the SDK code)
- ``apiVersion`` - version number of the SDK
- ``customErrors`` - a comma separated list of API specific exceptions in the form of <error name>:<status code> (e.g. ``NoEventAccess:401``)

### Validation

The Swagger project provides a [online editor](http://editor.swagger.io/) that parses and validations a Swagger specification. Use the ``compile_spec.js`` to generate a complete specification for your target API and paste that into the Swagger editor to see the result and check if there is any errors. 

## Scripts

### compile_spec.js
This script compiles all definition files within an API folder into a single Swagger specification file. This is needed because the Swagger specification and the Swagger codegen library expects the complete API definition be in a single file.

For example, to compile the EventService V1 definition:

    node scripts/compile_spec.js apis/event_service/v1

The above command will print the complied definition to screen. You may provide an optional output path as the second parameter to have the output saved as a file.

    node scripts/compile_spec.js apis/event_service/v1 /tmp/event_service_v1_spec.yml

### generate_sdk.js
This script compiles (using ``compile_spec.js``) the API definition and generates the target SDK using the [Swagger Codegen](https://github.com/swagger-api/swagger-codegen) library.

```

OPTIONS
  -r <respository root path>
      (optional) path to the root of the novacast-sdk repository. Default to current directory.
  -s <spec directory>, --spec <spec directory>
      path to the API definition folder
  -o <output directory>, --output <output directory>
      path to the output directory
  -l <language>, --lang <language>
      target language of the SDK (sdkRuby or sdkJs)
  --debug <mode>
      enable debug mode.
      List of modes:
        'Swagger' - prints the swagger specification as interpreted by the codegen
        'Models' - prints models passed to the template engine
        'Operations' - prints operations passed to the template engine
        'SupportingFiles' - prints additional data passed to the template engine

```
For example, to generate the Ruby SDK for EventService V1:

    node scripts/generate_sdk.js -s apis/event_service/v1 -l sdkRuby -o /tmp/sdkRuby
