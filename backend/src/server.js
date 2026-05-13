import dotenv from 'dotenv';
import app from './app.js';
import connectDB from './config/db.js';
import { startMetaCronJob } from './jobs/metaSyncJob.js';

// Load environment variables
dotenv.config();

// Connect to Database
connectDB();

// Initialize CRON jobs
startMetaCronJob();

const PORT = process.env.PORT || 5000;

app.listen(PORT, () => {
    console.log(`Server running in ${process.env.NODE_ENV} mode on port ${PORT}`);
});
