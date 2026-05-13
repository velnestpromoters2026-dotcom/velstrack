import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import authRoutes from './routes/auth.routes.js';
import callRoutes from './routes/calls.routes.js';
import adminRoutes from './routes/admin.routes.js';

const app = express();

// Security and utility middlewares
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

// Basic health check endpoint
app.get('/health', (req, res) => {
    res.status(200).json({ status: 'OK', message: 'Velstrack API is running' });
});

// API Routes
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/calls', callRoutes);
app.use('/api/v1/admin', adminRoutes);

export default app;
