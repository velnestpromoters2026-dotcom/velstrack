import jwt from 'jsonwebtoken';

export const generateToken = (id, role) => {
    return jwt.sign({ id, role }, process.env.JWT_SECRET || 'velstrack_fallback_secret', {
        expiresIn: '30d',
    });
};
