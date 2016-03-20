module.exports = function(grunt) {
  grunt.initConfig({
    "bower-install-simple": {
      options: {
        directory: "bower_components/public"
      },
      "prod": {
        options: {
          production: true
        }
      },
      "dev": {
        options: {
          production: false
        }
      }
    },
    wiredep: {
      task: {
        src: ['../resources/templates/**/*.jade'],
        directory: 'bower_components/public',
        ignorePath: '../../web/bower_components/public'
      }
    }
  });

  grunt.loadNpmTasks('grunt-bower-install-simple');
  grunt.loadNpmTasks('grunt-wiredep');

  grunt.registerTask('default', 'build');
  grunt.registerTask('build', ['bower-install-simple', 'wiredep']);
};
