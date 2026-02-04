if (!process.env.CHROME_BIN) {
    try {
        process.env.CHROME_BIN = require('puppeteer').executablePath();
    } catch (e) {
        console.warn('Puppeteer não encontrado. O Chrome deve estar disponível no sistema.');
    }
}

module.exports = function (config) {
    config.set({
        basePath: '',

        frameworks: ['jasmine'],

        plugins: [
            require('karma-jasmine'),
            require('karma-chrome-launcher'),
            require('karma-jasmine-html-reporter'),
            require('karma-coverage')
        ],

        client: {
            jasmine: {},
            clearContext: false
        },

        coverageReporter: {
            dir: require('path').join(__dirname, './coverage/elandro-music'),
            subdir: '.',
            reporters: [
                { type: 'html' },
                { type: 'text-summary' }
            ]
        },

        customLaunchers: {
            ChromeHeadlessNoSandbox: {
                base: 'ChromeHeadless',
                flags: [
                    '--no-sandbox',
                    '--disable-setuid-sandbox'
                ]
            }
        },

        browsers: ['ChromeHeadlessNoSandbox'],

        reporters: ['progress', 'kjhtml'],

        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,

        autoWatch: false,
        singleRun: true,
        restartOnFileChange: false
    });
};
