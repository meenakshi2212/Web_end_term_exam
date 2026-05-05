const http = require('http');

async function request(path, method = 'GET', data = null) {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: 'localhost',
      port: 8080,
      path: path,
      method: method,
      headers: {
        'Content-Type': 'application/json'
      }
    };

    const req = http.request(options, res => {
      let body = '';
      res.on('data', chunk => body += chunk);
      res.on('end', () => resolve({ status: res.statusCode, data: JSON.parse(body || '{}') }));
    });

    req.on('error', e => reject(e));
    if (data) req.write(JSON.stringify(data));
    req.end();
  });
}

async function run() {
  console.log("Creating user A (alice)...");
  let aliceId = null;
  const resAlice = await request('/api/auth/signup', 'POST', { username: 'alice', email: 'alice@example.com', displayName: 'Alice', password: 'password123' });
  if (resAlice.status === 200) {
     aliceId = resAlice.data.userId;
     console.log("Alice created successfully.");
  } else if (resAlice.status === 400 && resAlice.data.error.includes("taken")) {
     console.log("Alice already exists. Logging in.");
     const resLogin = await request('/api/auth/login', 'POST', { username: 'alice', password: 'password123' });
     aliceId = resLogin.data.userId;
  } else {
     console.error("Failed to create Alice:", resAlice);
     return;
  }

  console.log("Creating user B (bob)...");
  let bobId = null;
  const resBob = await request('/api/auth/signup', 'POST', { username: 'bob', email: 'bob@example.com', displayName: 'Bob', password: 'password123' });
  if (resBob.status === 200) {
     bobId = resBob.data.userId;
     console.log("Bob created successfully.");
  } else if (resBob.status === 400 && resBob.data.error.includes("taken")) {
     console.log("Bob already exists. Logging in.");
     const resLogin = await request('/api/auth/login', 'POST', { username: 'bob', password: 'password123' });
     bobId = resLogin.data.userId;
  } else {
     console.error("Failed to create Bob:", resBob);
     return;
  }

  console.log("Alice creates a note...");
  const resNote = await request('/api/notes', 'POST', { ownerId: aliceId, title: 'Secret Plan', content: 'We launch at dawn.' });
  const noteId = resNote.data.id;
  console.log("Note created with ID:", noteId);

  console.log("Bob fetches his notes...");
  let resBobNotes = await request(`/api/notes?ownerId=${bobId}`);
  console.log("Bob's notes before sharing:", resBobNotes.data.map(n => n.id));

  console.log("Alice shares the note with Bob...");
  const resShare = await request('/api/share', 'POST', { noteId: noteId, username: 'bob' });
  console.log("Share response:", resShare.data);

  console.log("Bob fetches his notes again...");
  resBobNotes = await request(`/api/notes?ownerId=${bobId}`);
  console.log("Bob's notes after sharing:", resBobNotes.data.map(n => n.id));
  
  if (resBobNotes.data.find(n => n.id === noteId)) {
      console.log("SUCCESS! Shared note appeared in Bob's notes.");
  } else {
      console.error("FAILURE! Shared note did NOT appear in Bob's notes.");
  }
}

run().catch(console.error);
