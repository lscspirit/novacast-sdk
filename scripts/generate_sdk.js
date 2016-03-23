var argv = require('minimist')(process.argv.slice(2));

var child_process = require('child_process');
var tmp  = require('tmp');
var path = require('path');

var root_path   = argv['r'] || path.resolve("./");
var spec_path   = argv['s'] || argv['spec'];
var output_path = argv['o'] || argv['output'];
var debug_mode  = argv['debug'];
var lang = argv['l'] || argv['lang'] || 'sdkRuby';

var swagger_lib = `${root_path}/swagger-codegen/target`;

function execCmd(cmd) {
  console.log('===== Executing: ' + cmd + ' ======\n');
  child_process.execSync(cmd, { stdio: [0,1,2] });
  console.log('\n');
}

var temp_file = tmp.fileSync();

// clear the output directory
execCmd(`rm -rf ${output_path}`);

// Compile the spec into a single temp file
execCmd(`node ${__dirname}/compile_spec.js ${spec_path} ${temp_file.name}`);

// Generate the sdk from the compiled spec
var classpaths  = `${swagger_lib}/novacast-sdk-swagger-codegen-1.0.0.jar:${swagger_lib}/dependency/*`;
var config_path = path.join(spec_path, 'config.json');
var debug_flag  = debug_mode ? `-Ddebug${debug_mode}` : '';
var cmd = `java ${debug_flag} -cp '${classpaths}' io.swagger.codegen.Codegen -l ${lang} -i ${temp_file.name} -c ${config_path} -o ${output_path}`;
execCmd(cmd);
