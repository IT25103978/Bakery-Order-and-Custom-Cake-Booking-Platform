document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/admin/analytics/customers')
        .then(response => response.json())
        .then(data => {
            const ctx = document.getElementById('customerGrowthChart').getContext('2d');
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: ['May 1', 'May 2', 'May 3', 'May 4', 'May 5'],
                    datasets: [{
                        label: 'New Customers',
                        data: [8, 12, 5, 15, 10],
                        borderColor: '#3b82f6',
                        tension: 0.4
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false }
            });
        });
});
