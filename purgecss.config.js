const path = require('path');

module.exports = {
    content: [
        path.resolve(__dirname, 'src/main/resources/views/admin/**/*.fxml'),
        path.resolve(__dirname, 'src/main/resources/views/coordinator/**/*.fxml'),
        path.resolve(__dirname, 'src/main/resources/views/shared/*.fxml'),
    ],
    css: [path.resolve(__dirname, 'src/main/resources/styles/global-style.css')],
    output: path.resolve(__dirname, 'src/main/resources/styles/global-style.cleaned.css')
};
