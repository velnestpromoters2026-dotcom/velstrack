import axios from 'axios';
import MetaCampaign from '../models/MetaCampaign.js';

export const fetchMetaInsights = async () => {
    try {
        const adAccountId = process.env.META_AD_ACCOUNT_ID;
        const version = process.env.META_API_VERSION || 'v19.0';
        const accessToken = process.env.META_ACCESS_TOKEN;
        
        const url = `https://graph.facebook.com/${version}/${adAccountId}/insights`;
        
        const response = await axios.get(url, {
            params: {
                fields: 'impressions,clicks,spend,reach',
                date_preset: 'maximum',
                access_token: accessToken
            }
        });

        const data = response.data.data;
        if (data && data.length > 0) {
            const insights = data[0]; 
            
            // Upsert the global account insight as a generic campaign for the MVP dashboard
            await MetaCampaign.findOneAndUpdate(
                { campaignId: adAccountId }, 
                {
                    name: 'Global Account Metrics',
                    status: 'ACTIVE',
                    dailyBudget: parseFloat(insights.spend || 0) // Storing aggregated spend here for simplicity
                },
                { upsert: true, new: true }
            );

            console.log(`Successfully synced Meta Ads Insights. Spend: $${insights.spend}, Clicks: ${insights.clicks}`);
            return insights;
        }
    } catch (error) {
        console.error('Error fetching Meta Ads Insights:', error.response?.data || error.message);
    }
};
