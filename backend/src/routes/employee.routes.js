import express from 'express';
import { getEmployeeDashboardStats } from '../controllers/employee.controller.js';
import { protect } from '../middlewares/auth.middleware.js';

const router = express.Router();

router.get('/dashboard', protect, getEmployeeDashboardStats);

export default router;
