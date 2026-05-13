import express from 'express';
import { syncCalls } from '../controllers/calls.controller.js';
import { protect } from '../middlewares/auth.middleware.js';

const router = express.Router();

router.post('/sync', protect, syncCalls);

export default router;
