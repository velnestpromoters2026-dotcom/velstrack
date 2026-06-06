import mongoose from 'mongoose';
import bcrypt from 'bcryptjs';
import dotenv from 'dotenv';
import User from './src/models/User.js';

dotenv.config();

const createUsers = async () => {
    try {
        await mongoose.connect(process.env.MONGO_URI);
        console.log('MongoDB connected');

        const salt = await bcrypt.genSalt(10);
        const adminPassword = await bcrypt.hash('admin123', salt);
        const employeePassword = await bcrypt.hash('employee123', salt);

        // Delete existing mock users
        await User.deleteMany({ email: { $in: ['admin@velstrack.com', 'employee@velstrack.com'] } });

        await User.create({
            email: 'admin@velstrack.com',
            passwordHash: adminPassword,
            role: 'ADMIN',
            profile: { firstName: 'Admin', lastName: 'User', phone: '1234567890' }
        });

        await User.create({
            email: 'employee@velstrack.com',
            passwordHash: employeePassword,
            role: 'EMPLOYEE',
            profile: { firstName: 'Test', lastName: 'Employee', phone: '0987654321' }
        });

        console.log('Default users created successfully!');
        process.exit();
    } catch (error) {
        console.error('Error creating users:', error);
        process.exit(1);
    }
};

createUsers();
