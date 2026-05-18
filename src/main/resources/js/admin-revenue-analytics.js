document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/admin/analytics/revenue')
        .then(response => response.json())
        .then(data => {
            const ctx = document.getElementById('revenueByDateChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
                    datasets: [{
                        label: 'Revenue ($)',
                        data: [1200, 1900, 1500, 2200],
                        backgroundColor: '#10b981',
                        borderRadius: 5
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false }
            });
        });
});
