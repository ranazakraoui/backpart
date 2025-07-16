document.addEventListener('DOMContentLoaded', function() {
    // Handle Add Formation
    document.getElementById('saveFormation').addEventListener('click', function() {
        const formationData = {
            titre: document.getElementById('titre').value,
            description: document.getElementById('description').value,
            duree: parseInt(document.getElementById('duree').value),
            planifiee: document.getElementById('planifiee').value === 'true'
        };

        fetch('/api/formations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formationData)
        })
            .then(response => response.json())
            .then(data => {
                alert('Formation ajoutée avec succès !');
                window.location.reload();
            })
            .catch(error => {
                console.error('Erreur:', error);
                alert('Erreur lors de l\'ajout de la formation.');
            });

        // Close modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('addFormationModal'));
        modal.hide();
    });

    // Handle Edit Formation
    document.querySelectorAll('.edit-formation').forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');

            fetch(`/api/formations/${id}`)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('editId').value = data.id;
                    document.getElementById('editTitre').value = data.titre;
                    document.getElementById('editDescription').value = data.description;
                    document.getElementById('editDuree').value = data.duree;
                    document.getElementById('editPlanifiee').value = data.planifiee.toString();

                    const modal = new bootstrap.Modal(document.getElementById('editFormationModal'));
                    modal.show();
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    alert('Erreur lors du chargement des données de la formation.');
                });
        });
    });

    // Handle Update Formation
    document.getElementById('updateFormation').addEventListener('click', function() {
        const id = document.getElementById('editId').value;
        const formationData = {
            titre: document.getElementById('editTitre').value,
            description: document.getElementById('editDescription').value,
            duree: parseInt(document.getElementById('editDuree').value),
            planifiee: document.getElementById('editPlanifiee').value === 'true'
        };

        fetch(`/api/formations/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formationData)
        })
            .then(response => {
                if (response.ok) {
                    alert('Formation mise à jour avec succès !');
                    window.location.reload();
                } else {
                    throw new Error('Erreur lors de la mise à jour');
                }
            })
            .catch(error => {
                console.error('Erreur:', error);
                alert('Erreur lors de la mise à jour de la formation.');
            });

        // Close modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('editFormationModal'));
        modal.hide();
    });

    // Handle Delete Formation
    document.querySelectorAll('.delete-formation').forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            if (confirm('Voulez-vous vraiment supprimer cette formation ?')) {
                fetch(`/api/formations/${id}`, {
                    method: 'DELETE'
                })
                    .then(response => {
                        if (response.ok) {
                            alert('Formation supprimée avec succès !');
                            window.location.reload();
                        } else {
                            throw new Error('Erreur lors de la suppression');
                        }
                    })
                    .catch(error => {
                        console.error('Erreur:', error);
                        alert('Erreur lors de la suppression de la formation.');
                    });
            }
        });
    });
});