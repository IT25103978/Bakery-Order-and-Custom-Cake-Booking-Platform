document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/admin/analytics/products')
        .then(response => response.json())
        .then(data => {
            const ctx = document.getElementById('productPerformanceChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ['Chocolate Cake', 'Vanilla Cupcakes', 'Brownies', 'Butter Cake'],
                    datasets: [{
                        label: 'Quantity Sold',
                        data: [320, 280, 210, 180],
                        backgroundColor: '#8b5cf6',
                        borderRadius: 5
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false }
            });
        });
});
