/**
 * NMDLock Device Routes
 * POST /device/register  - Register device
 * GET  /device/status    - Get device status
 * GET  /device/history   - Get device activity history
 */
const express = require('express');
const router = express.Router();
const deviceController = require('../controllers/deviceController');

router.post('/register', deviceController.register);
router.get('/status', deviceController.status);
router.get('/history', deviceController.history);

module.exports = router;
