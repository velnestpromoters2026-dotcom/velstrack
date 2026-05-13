import express from 'express';
import { getDashboardStats } from '../controllers/admin.controller.js';
import { protect, adminOnly } from '../middlewares/auth.middleware.js';

const router = express.Router();

// Require both valid JWT and ADMIN role
router.get('/dashboard', protect, adminOnly, getDashboardStats);

export default router;
