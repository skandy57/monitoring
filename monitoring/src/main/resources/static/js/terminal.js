/**
 * 
 */
document.addEventListener('DOMContentLoaded', function () {
    const outputDiv = document.getElementById('output');
    const inputField = document.getElementById('command-input');

    inputField.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            const command = inputField.value;
            inputField.value = '';

            // Process the command (you can customize this part)
            const outputText = processCommand(command);

            // Display the output
            appendToOutput(outputText);
        }
    });

    function appendToOutput(text) {
        const outputLine = document.createElement('div');
        outputLine.textContent = text;
        outputDiv.appendChild(outputLine);

        // Scroll to the bottom to show the latest output
        outputDiv.scrollTop = outputDiv.scrollHeight;
    }

    function processCommand(command) {
        // Implement your command processing logic here
        // For simplicity, just echoing the command for now
        return `$ ${command}`;
    }
});