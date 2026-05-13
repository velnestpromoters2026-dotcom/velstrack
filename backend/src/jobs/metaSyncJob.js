import cron from 'node-cron';
import { fetchMetaInsights } from '../services/meta.ads.service.js';

export const startMetaCronJob = () => {
    // Run every day at midnight '0 0 * * *'
    cron.schedule('0 0 * * *', async () => {
        console.log('--- CRON: Starting Meta Ads API Sync ---');
        await fetchMetaInsights();
        console.log('--- CRON: Meta Ads API Sync Completed ---');
    });
    
    console.log('Meta Ads Sync Cron Job Initialized (Runs at 00:00 daily).');
};
