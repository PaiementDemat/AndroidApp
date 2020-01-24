# Mobile Pay

## Fonctionnel
- Menu latéral
- Gestion des autorisations (Camera, NFC, paramètres système)
- Lecture de QR Code
- Génération de QR Code en fonction d'un montant, luminosité maximale lorsque le QR est prêt
- Lecture de tags NFC Ndef (doivent être pour le moment de type "text/plain")
- Ouverture automatique de la fenêtre de paiement lors de la détection d'un tag adapté
- Confirmation de paiement via empreinte digitale ou saisie d'un PIN

- Liaison au Back-End
  - Login
  - Inscription
  - Gestion du solde du compte utilisateur
  - Génération du code QR commerçant
  - Paiement via scan du code QR ou d'un tag NFC

## En cours
- Paramètres
- Historique de transactions

## Futurs développements
- Gestion transactions multi utilisateurs
- Profils utilisateurs
