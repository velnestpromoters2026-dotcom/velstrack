import express from 'express';
import { getDashboardStats, getEmployees, addEmployee, getMetaCampaigns, getTargets, createTarget, updateTarget, getAnalytics, getMetaStatus } from '../controllers/admin.controller.js';
import { protect, adminOnly } from '../middlewares/auth.middleware.js';

const router = express.Router();

router.use(protect, adminOnly);

router.get('/dashboard', getDashboardStats);
router.get('/employees', getEmployees);
router.post('/employees', addEmployee);
router.get('/meta/campaigns', getMetaCampaigns);
router.get('/meta/status', getMetaStatus);

router.get('/targets', getTargets);
router.post('/targets', createTarget);
router.patch('/targets/:id', updateTarget);

router.get('/analytics', getAnalytics);

export default router;
