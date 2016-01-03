var argv = require('minimist')(process.argv.slice(2));

var child_process = require('child_process');
var tmp  = require('tmp');
var path = require('path');

var swagger_dist   = argv['dist'];
var custom_codegen = argv['jar'];
var spec_path   = argv['s'] || argv['spec'];
var output_path = argv['o'] || argv['output'];
var debug_mode  = argv['debug'];
var lang = argv['l'] || argv['lang'] || 'sdkRuby';

function execCmd(cmd) {
  console.log('===== Executing: ' + cmd + ' ======\n');
  child_process.execSync(cmd, { stdio: [0,1,2] });
  console.log('\n');
}

// swagger classpaths
var swagger_classes = path.join(swagger_dist, 'target/classes');
var swagger_lib     = path.join(swagger_dist, 'target/lib') + '/*';

var temp_file = tmp.fileSync();

// Compile the spec into a single temp file
execCmd(`node ${__dirname}/compile_spec.js ${spec_path} ${temp_file.name}`);

// Generate the sdk from the compiled spec
var classpaths  = `${swagger_classes}:${swagger_lib}:${custom_codegen}`;
var config_path = path.join(spec_path, 'config.json');
var debug_flag  = debug_mode ? `-Ddebug${debug_mode}` : '';
var cmd = `java ${debug_flag} -cp ${classpaths} io.swagger.codegen.Codegen -l ${lang} -i ${temp_file.name} -c ${config_path} -o ${output_path}`;
execCmd(cmd);
