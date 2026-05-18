document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/admin/analytics/orders')
        .then(response => response.json())
        .then(data => {
            const ctx = document.getElementById('ordersByDateChart').getContext('2d');
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: ['May 1', 'May 2', 'May 3', 'May 4', 'May 5', 'May 6', 'May 7'],
                    datasets: [{
                        label: 'Orders',
                        data: [12, 19, 15, 22, 18, 25, 20],
                        borderColor: '#8b5cf6',
                        backgroundColor: 'rgba(139, 92, 246, 0.2)',
                        fill: true,
                        tension: 0.4
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false }
            });
        });
});
