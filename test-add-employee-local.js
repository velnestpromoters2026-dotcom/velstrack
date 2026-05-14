async function test() {
    try {
        const res = await fetch('http://localhost:5000/api/v1/admin/employees', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: "Test User",
                email: "test55567@test.com",
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
