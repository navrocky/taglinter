module.exports = [{
    name: 'taglinter-cli',
    entry: './cli/build/compileSync/js/main/productionExecutable/kotlin/taglinter-cli.js',
    target: 'node', // <-- importat part!
    mode: 'production',
    output: {
        path: __dirname + '/dist/',
        filename: 'taglinter-cli.js',
    }
}];