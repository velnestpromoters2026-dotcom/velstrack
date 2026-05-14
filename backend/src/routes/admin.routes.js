import express from 'express';
import { getDashboardStats, getEmployees, addEmployee, getMetaCampaigns } from '../controllers/admin.controller.js';
import { protect, adminOnly } from '../middlewares/auth.middleware.js';

const router = express.Router();

router.use(protect, adminOnly);

router.get('/dashboard', getDashboardStats);
router.get('/employees', getEmployees);
router.post('/employees', addEmployee);
router.get('/meta/campaigns', getMetaCampaigns);

export default router;
