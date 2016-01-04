package com.novacast;

import io.swagger.codegen.*;
import io.swagger.models.properties.*;

import io.swagger.models.Swagger;
import io.swagger.models.Model;
import io.swagger.models.Operation;

import java.util.*;
import java.io.File;

import org.apache.commons.lang.StringUtils;

public class SdkRubyGenerator extends DefaultCodegen implements CodegenConfig {

  // source folder where to write the files
  protected String gemName = null;
  protected String moduleName = null;
  protected String gemVersion = "1.0.0";
  protected String libFolder = "lib";

  /**
   * Configures the type of generator.
   *
   * @return  the CodegenType for this generator
   * @see     io.swagger.codegen.CodegenType
   */
  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  /**
   * Configures a friendly name for the generator.  This will be used by the generator
   * to select the library with the -l flag.
   *
   * @return the friendly name for the generator
   */
  public String getName() {
    return "sdkRuby";
  }

  /**
   * Returns human-friendly help for the generator.  Provide the consumer with help
   * tips, parameters here
   *
   * @return A string value for the help message
   */
  public String getHelp() {
    return "Generates a sdkRuby client library.";
  }

  public SdkRubyGenerator() {
    super();

    // set the output folder here
    outputFolder = "generated-code/sdkRuby";

    /**
     * Models.  You can write model files using the modelTemplateFiles map.
     * if you want to create one template for file, you can do so here.
     * for multiple files for model, just put another entry in the `modelTemplateFiles` with
     * a different extension
     */
    modelTemplateFiles.put("model.mustache", ".rb");       // the extension for each file to write

    /**
     * Api classes.  You can write classes for each Api file with the apiTemplateFiles map.
     * as with models, add multiple entries with different extensions for multiple files per
     * class
     */
    apiTemplateFiles.put("api.mustache", ".rb");       // the extension for each file to write

    /**
     * Template Location.  This is the location which templates will be read from.  The generator
     * will use the resource stream to attempt to read the templates.
     */
    templateDir = "sdkRuby";

    /**
     * Api Package.  Optional, if needed, this can be used in templates
     */
    apiPackage = "api";

    /**
     * Model Package.  Optional, if needed, this can be used in templates
     */
    modelPackage = "models";

    /**
     * Reserved words.  Override this with reserved words specific to your language
     */
     reservedWords = new HashSet<String>(
       Arrays.asList(
         "__FILE__", "and", "def", "end", "in", "or", "self", "unless", "__LINE__",
         "begin", "defined?", "ensure", "module", "redo", "super", "until", "BEGIN",
         "break", "do", "false", "next", "rescue", "then", "when", "END", "case",
         "else", "for", "nil", "retry", "true", "while", "alias", "class", "elsif",
         "if", "not", "return", "undef", "yield")
     );

    /**
     * Language Specific Primitives.  These types will not trigger imports by
     * the client generator
     */
    languageSpecificPrimitives = new HashSet<String>(
      Arrays.asList("int", "array", "map", "string", "DateTime")
    );

    typeMapping.put("string", "String");
    typeMapping.put("char", "String");
    typeMapping.put("int", "Integer");
    typeMapping.put("integer", "Integer");
    typeMapping.put("long", "Integer");
    typeMapping.put("short", "Integer");
    typeMapping.put("float", "Float");
    typeMapping.put("double", "Float");
    typeMapping.put("number", "Float");
    typeMapping.put("date", "Date");
    typeMapping.put("DateTime", "DateTime");
    typeMapping.put("boolean", "BOOLEAN");
    typeMapping.put("array", "Array");
    typeMapping.put("List", "Array");
    typeMapping.put("map", "Hash");
    typeMapping.put("object", "Object");
    typeMapping.put("file", "File");

    // remove modelPackage and apiPackage added by default
    cliOptions.clear();
    cliOptions.add(new CliOption("gemName", "gem name, default: swagger_client"));
    cliOptions.add(new CliOption("sdkName", "sdk name, default: swagger_client"));
    cliOptions.add(new CliOption("moduleName", "top module name (convention: CamelCase, usually corresponding to gem name), default: SwaggerClient"));
    cliOptions.add(new CliOption("gemVersion", "gem version, default: 1.0.0"));
    cliOptions.add(new CliOption("apiVersion", "api version, default: 1.0.0"));
    cliOptions.add(new CliOption("customErrors", "custom error list (convention: comma separated list of <Error Name>:<HTTP Status>)"));
  }

  @Override
  public void processOpts() {
      super.processOpts();

      setSortParamsByRequiredFlag(new Boolean(true));

      // uses 'gemName' property as gemName if available
      // otherwise, uses 'sdkName' property
      if (additionalProperties.containsKey("gemName")) {
          setGemName((String) additionalProperties.get("gemName"));
      } else if (additionalProperties.containsKey("sdkName")) {
          setGemName((String) additionalProperties.get("sdkName"));
      }
      if (additionalProperties.containsKey("moduleName")) {
          setModuleName((String) additionalProperties.get("moduleName"));
      }

      // determines the gem and module names when either one or both are missing
      if (gemName == null && moduleName == null) {
          setGemName("swagger_client");
          setModuleName(generateModuleName(gemName));
      } else if (gemName == null) {
          setGemName(generateGemName(moduleName));
      } else if (moduleName == null) {
          setModuleName(generateModuleName(gemName));
      }

      // finalizes the gemName and moduleName
      additionalProperties.put("gemName", gemName);
      additionalProperties.put("moduleName", moduleName);

      // uses 'gemVersion' property as gemVersion if available
      // otherwise, uses 'apiVersion' property
      if (additionalProperties.containsKey("gemVersion")) {
          setGemVersion((String) additionalProperties.get("gemVersion"));
      } else if (additionalProperties.containsKey("apiVersion")) {
          setGemVersion((String) additionalProperties.get("apiVersion"));
      }

      // finalizes the gemVersion
      additionalProperties.put("gemVersion", gemVersion);

      if (additionalProperties.containsKey("customErrors")) {
          List<Map<String, String>> errList = new ArrayList<Map<String, String>>();
          String[] errSpecs = ((String) additionalProperties.get("customErrors")).split("[,\\s]+");

          for (String spec : errSpecs) {
            String[] details = spec.split(":");
            Map<String, String> err = new HashMap<String, String>();
            err.put("name", details[0]);
            err.put("status", details[1]);
            errList.add(err);
          }
          additionalProperties.put("customErrorList", errList);
      }

      additionalProperties.put("customErrorImportPath", gemName + "/errors.rb");

      // use constant model/api package (folder path)
      setModelPackage("models");
      setApiPackage("api");

      String gemFolder = libFolder + File.separator + gemName;

      supportingFiles.add(new SupportingFile("gemspec.mustache", "", gemName + ".gemspec"));
      supportingFiles.add(new SupportingFile("gem.mustache", libFolder, gemName + ".rb"));
      supportingFiles.add(new SupportingFile("version.mustache", gemFolder, "version.rb"));
      supportingFiles.add(new SupportingFile("errors.mustache", gemFolder, "errors.rb"));

      // API compliant Test Cases
      supportingFiles.add(new SupportingFile("paths_spec.mustache", "rspec/" + gemName, "paths_spec.rb"));
      supportingFiles.add(new SupportingFile("apis_spec_helper.mustache", "rspec/" + gemName, "apis_spec_helper.rb"));
  }

  /**
   * Escapes a reserved word as defined in the `reservedWords` array. Handle escaping
   * those terms here.  This logic is only called if a variable matches the reseved words
   *
   * @return the escaped term
   */
  @Override
  public String escapeReservedWord(String name) {
    return "_" + name;  // add an underscore to the name
  }

  /**
   * Location to write model files.  You can use the modelPackage() as defined when the class is
   * instantiated
   */
  public String modelFileFolder() {
    return outputFolder + File.separator + libFolder + File.separator + gemName + File.separator + modelPackage.replace("/", File.separator);
  }

  /**
   * Location to write api files.  You can use the apiPackage() as defined when the class is
   * instantiated
   */
  @Override
  public String apiFileFolder() {
    return outputFolder + File.separator + libFolder + File.separator + gemName + File.separator + apiPackage.replace("/", File.separator);
  }

  @Override
  public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, Map<String, Model> definitions, Swagger swagger) {
    CodegenOperation op = super.fromOperation(path, httpMethod, operation, definitions, swagger);

    // put paramters in this order: "path", other "required" then "optional"
    Collections.sort(op.allParams, new Comparator<CodegenParameter>() {
      @Override
      public int compare(CodegenParameter one, CodegenParameter another) {
        boolean oneRequired = one.required == null ? false : one.required;
        boolean oneIsPath   = one.isPathParam == null ? false : one.isPathParam;
        boolean anotherRequired = another.required == null ? false : another.required;
        boolean anotherIsPath   = another.isPathParam == null ? false : another.isPathParam;
        if (oneIsPath == anotherIsPath) {
          if (sortParamsByRequiredFlag != true) return 0;
          else if (oneRequired == anotherRequired) return 0;
          else if (oneRequired) return -1;
          else return 1;
        }
        else if (oneIsPath) return -1;
        else return 1;
      }
    });

    updateHasMore(op.allParams);

    return op;
  }

  /**
   * Optional - type declaration.  This is a String which is used by the templates to instantiate your
   * types.  There is typically special handling for different property types
   *
   * @return a string value used as the `dataType` field for model templates, `returnType` for api templates
   */
  @Override
  public String getTypeDeclaration(Property p) {
    if(p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      return getSwaggerType(p) + "[" + getTypeDeclaration(inner) + "]";
    }
    else if (p instanceof MapProperty) {
      MapProperty mp = (MapProperty) p;
      Property inner = mp.getAdditionalProperties();
      return getSwaggerType(p) + "[String, " + getTypeDeclaration(inner) + "]";
    }
    return super.getTypeDeclaration(p);
  }

  /**
   * Optional - swagger type conversion.  This is used to map swagger types in a `Property` into
   * either language specific types via `typeMapping` or into complex models if there is not a mapping.
   *
   * @return a string value of the type or complex model for this property
   * @see io.swagger.models.properties.Property
   */
  @Override
  public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if(typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if(languageSpecificPrimitives.contains(type))
        return toModelName(type);
    }
    else
      type = swaggerType;
    return toModelName(type);
  }

  public String toDefaultValue(Property p) {
    return "null";
  }

  @Override
  public String toVarName(String name) {
    // sanitize name
    name = sanitizeName(name);

    // if it's all uppper case, convert to lower case
    if (name.matches("^[A-Z_]*$")) {
      name = name.toLowerCase();
    }

    // camelize (lower first character) the variable name
    // petId => pet_id
    name = underscore(name);

    // for reserved word or word starting with number, append _
    if (reservedWords.contains(name) || name.matches("^\\d.*")) {
      name = escapeReservedWord(name);
    }

    return name;
  }

  @Override
  public String toParamName(String name) {
    // should be the same as variable name
    return toVarName(name);
  }

  @Override
  public String toModelName(String name) {
    name = sanitizeName(name);

    // model name cannot use reserved keyword, e.g. return
    if (reservedWords.contains(name)) {
      throw new RuntimeException(name + " (reserved word) cannot be used as a model name");
    }

    // camelize the model name
    // phone_number => PhoneNumber
    return camelize(name);
  }

  @Override
  public String toModelFilename(String name) {
    // model name cannot use reserved keyword, e.g. return
    if (reservedWords.contains(name)) {
      throw new RuntimeException(name + " (reserved word) cannot be used as a model name");
    }

    // underscore the model file name
    // PhoneNumber.rb => phone_number.rb
    return underscore(name);
  }

  @Override
  public String toApiFilename(String name) {
    // replace - with _ e.g. created-at => created_at
    name = name.replaceAll("-", "_");

    // e.g. PhoneNumberApi.rb => phone_number_api.rb
    return underscore(name) + "_api";
  }

  @Override
  public String toApiName(String name) {
    if (name.length() == 0) {
      return "Api";
    }
    // e.g. phone_number_api => PhoneNumberApi
    return camelize(name) + "Api";
  }

  @Override
  public String toOperationId(String operationId) {
    // throw exception if method name is empty
    if (StringUtils.isEmpty(operationId)) {
      throw new RuntimeException("Empty method name (operationId) not allowed");
    }

    // method name cannot use reserved keyword, e.g. return
    if (reservedWords.contains(operationId)) {
      throw new RuntimeException(operationId + " (reserved word) cannot be used as method name");
    }

    return underscore(sanitizeName(operationId));
  }

  @Override
  public String toModelImport(String name) {
    return gemName + "/" + modelPackage() + "/" + toModelFilename(name);
  }

  @Override
  public String toApiImport(String name) {
    return gemName + "/" + apiPackage() + "/" + toApiFilename(name);
  }

  public void setGemName(String gemName) {
    this.gemName = gemName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public void setGemVersion(String gemVersion) {
    this.gemVersion = gemVersion;
  }

  /**
   * Generate Ruby module name from the gem name, e.g. use "SwaggerClient" for "swagger_client".
   */
  public String generateModuleName(String gemName) {
    return camelize(gemName.replaceAll("[^\\w]+", "_"));
  }

  /**
  * Generate Ruby gem name from the module name, e.g. use "swagger_client" for "SwaggerClient".
  */
  public String generateGemName(String moduleName) {
    return underscore(moduleName.replaceAll("[^\\w]+", ""));
  }

  /**
   * sanitize name (parameter, property, method, etc)
   *
   * @param name string to be sanitize
   * @return sanitized string
   */
  public String sanitizeName(String name) {
    // NOTE: performance wise, we should have written with 2 replaceAll to replace desired
    // character with _ or empty character. Below aims to spell out different cases we've
    // encountered so far and hopefully make it easier for others to add more special
    // cases in the future.

    // input[] => input
    name = name.replaceAll("\\[\\]", "");

    // input[a][b] => input_a_b
    name = name.replaceAll("\\[", "_");
    name = name.replaceAll("\\]", "");

    // input(a)(b) => input_a_b
    name = name.replaceAll("\\(", "_");
    name = name.replaceAll("\\)", "");

    // input.name => input_name
    name = name.replaceAll("\\.", "_");

    // input-name => input_name
    name = name.replaceAll("-", "_");

    // input name and age => input_name_and_age
    name = name.replaceAll(" ", "_");

    // remove everything else other than word, number and _
    // $php_variable => php_variable
    return name.replaceAll("[^a-zA-Z0-9_]", "");
  }

  private static void updateHasMore(List<CodegenParameter> objs) {
    if (objs != null) {
        for (int i = 0; i < objs.size(); i++) {
            objs.get(i).secondaryParam = (i > 0) ? (new Boolean(true)) : null;
            objs.get(i).hasMore = (i < objs.size() - 1) ? (new Boolean(true)) : null;
        }
    }
  }
}
