async function test() {
    try {
        const loginRes = await fetch('https://velstrack-api.onrender.com/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: "admin@velstrack.com", password: "admin" }) // Assuming admin credentials
        });
        const loginData = await loginRes.json();
        
        if (!loginData.success || !loginData.data.token) {
            console.log("Login failed:", loginData);
            return;
        }

        const token = loginData.data.token;
        console.log("Got token!");

        const res = await fetch('https://velstrack-api.onrender.com/api/v1/admin/employees', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                name: "Test User",
                email: "test555@test.com",
                phone: "1234567890",
                password: "password123",
                role: "EMPLOYEE",
                department: "Sales"
            })
        });
        const text = await res.text();
        console.log("Status:", res.status);
        console.log("Body:", text);
    } catch (e) {
        console.log("Error:", e.message);
    }
}
test();
