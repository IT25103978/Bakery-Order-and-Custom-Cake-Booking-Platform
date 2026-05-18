document.addEventListener('DOMContentLoaded', () => {
    // Fetch Summary Data
    fetch('/api/admin/dashboard/summary')
        .then(response => response.json())
        .then(data => {
            document.getElementById('total-orders').textContent = data.totalOrders;
            document.getElementById('total-products').textContent = data.totalProducts;
            document.getElementById('total-users').textContent = data.totalUsers;
            document.getElementById('total-revenue').textContent = '$' + parseFloat(data.totalRevenue).toFixed(2);
        })
        .catch(error => console.error('Error fetching summary:', error));

    // Fetch Recent Orders
    fetch('/api/admin/dashboard/recent-orders')
        .then(response => response.json())
        .then(orders => {
            const tableBody = document.getElementById('recent-orders-table-body');
            tableBody.innerHTML = '';
            
            if (orders.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="4" class="text-center">No recent orders found.</td></tr>';
                return;
            }
            
            orders.forEach(order => {
                let badgeClass = 'badge-pending';
                if (order.orderStatus === 'Preparing') badgeClass = 'badge-preparing';
                else if (order.orderStatus === 'Ready') badgeClass = 'badge-ready';
                else if (order.orderStatus === 'Delivered') badgeClass = 'badge-delivered';
                else if (order.orderStatus === 'Cancelled') badgeClass = 'badge-cancelled';
                
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>ORD-${order.orderId}</td>
                    <td>${new Date(order.createdAt).toLocaleDateString()}</td>
                    <td><span class="badge ${badgeClass}">${order.orderStatus}</span></td>
                    <td>$${order.totalAmount.toFixed(2)}</td>
                `;
                tableBody.appendChild(tr);
            });
        })
        .catch(error => console.error('Error fetching recent orders:', error));

    // Fetch Status Overview for Chart
    fetch('/api/admin/dashboard/status-overview')
        .then(response => response.json())
        .then(data => {
            const ctx = document.getElementById('orderStatusChart').getContext('2d');
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: ['Pending', 'Preparing', 'Completed', 'Cancelled'],
                    datasets: [{
                        data: [
                            data.Pending || 0,
                            data.Preparing || 0,
                            data.Completed || 0,
                            data.Cancelled || 0
                        ],
                        backgroundColor: [
                            '#f59e0b', // Warning/Pending
                            '#3b82f6', // Info/Preparing
                            '#10b981', // Success/Completed
                            '#ef4444'  // Danger/Cancelled
                        ],
                        borderWidth: 0
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'right'
                        }
                    },
                    cutout: '70%'
                }
            });
        })
        .catch(error => console.error('Error fetching chart data:', error));
});
