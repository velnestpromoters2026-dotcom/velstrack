import express from 'express';
import { getMetaCampaigns } from '../controllers/admin.controller.js';
import { protect, adminOnly } from '../middlewares/auth.middleware.js';

const router = express.Router();

router.get('/campaigns', protect, adminOnly, getMetaCampaigns);

export default router;
