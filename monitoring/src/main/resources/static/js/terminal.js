/**
 * 
 */
  const term = new Terminal();
        term.open(document.getElementById('terminal-container'));
        
        const socket = new WebSocket('ws://localhost:3000'); // Change the URL according to your server
        socket.onopen = () => {
            term.write('Terminal Connected\n\r');
            term.prompt = () => {
                term.write('\n\r$ ');
            };
            term.prompt();
        };
        
        socket.onmessage = (e) => {
            term.write(e.data);
            term.prompt();
        };
        
        term.onData((data) => {
            socket.send(data);
        });