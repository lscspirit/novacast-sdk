var argv = require('minimist')(process.argv.slice(2));

var resolve  = require('json-refs').resolveRefs;
var traverse = require('traverse');
var extend  = require('extend');
var YAML    = require('js-yaml');
var path    = require('path');
var fs      = require('fs');

// Argument values
var spec_path   = path.resolve(argv['_'][0]);
var output_file = argv['_'].length > 1 ? argv['_'][1] : null;

//
// Merges all paths and models
//
function mergeDirFiles(dir_path) {
  var files = fs.readdirSync(dir_path);
  return files.reduce(function(result, filename) {
    var content = YAML.load(fs.readFileSync(path.join(dir_path, filename)).toString());
    return extend(result, content);
  }, {});
}

var models = mergeDirFiles(path.join(spec_path, 'models'));
var paths  = mergeDirFiles(path.join(spec_path, 'paths'));

//
// Load the spec main file
//
var spec = YAML.load(fs.readFileSync(path.join(spec_path, 'spec.yml')).toString());

//
// Pre-process the spec and replace the $spec_ref values
//
traverse(spec).forEach(function(value) {
  if (this.key == '$spec_ref') {
    var ref = null;
    switch(value) {
      case 'models':
        ref = models;
        break;
      case 'paths':
        ref = paths;
        break;
      default:
        break;
    }

    if (ref) {
      this.delete(true);
      this.parent.update(extend(this.parent.node, ref), true);
    }
  }
});

//
// Resolve external/remote refs
//
var options = {
  location: spec_path,
  resolveLocalRefs: false,
  processContent: function (content) {
    return YAML.load(content);
  }
};
resolve(spec, options).then(function (results) {
  if (output_file) fs.writeFileSync(output_file, YAML.dump(results.resolved));
  else console.log(YAML.dump(results.resolved));
});
