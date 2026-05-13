import User from '../models/User.js';
import { generateToken } from '../utils/jwt.js';

export const loginUser = async (req, res) => {
    const { email, password } = req.body;
    try {
        const user = await User.findOne({ email });
        if (user && (await user.matchPassword(password))) {
            user.lastLogin = new Date();
            await user.save();
            
            res.json({
                _id: user._id,
                email: user.email,
                role: user.role,
                profile: user.profile,
                token: generateToken(user._id, user.role),
            });
        } else {
            res.status(401).json({ message: 'Invalid email or password' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Server Error' });
    }
};

export const registerUser = async (req, res) => {
    const { email, password, role, profile } = req.body;
    try {
        const userExists = await User.findOne({ email });
        if (userExists) return res.status(400).json({ message: 'User already exists' });

        const bcrypt = await import('bcryptjs');
        const salt = await bcrypt.default.genSalt(10);
        const passwordHash = await bcrypt.default.hash(password, salt);

        const user = await User.create({ email, passwordHash, role, profile });
        
        if (user) {
            res.status(201).json({
                _id: user._id,
                email: user.email,
                role: user.role,
                token: generateToken(user._id, user.role),
            });
        } else {
            res.status(400).json({ message: 'Invalid user data' });
        }
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};
