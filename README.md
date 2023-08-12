# VANAD-Call-Center

## Présentation des données

Nous disposons de données qui nous proviennent du centre d’appel du VANAD Laboratories (Rotterdam, aux Pays-Bas). Les données sont collectées sur la période d’une année, de janvier 2014 à décembre 2014. 

Ce centre d’appels est multi-compétences (ie. reçoit plusieurs types de service), fonctionne de 8H à 20H du lundi au vendredi, reçoit au total 27 types d’appels (types de service), et 312 agents au total. Chaque agent possède un ensemble de compétences, qui correspond à l’ensemble des types d’appels qu’il peut servir. Nous disposons de deux jeux de données distincts: des données sur les appels entrants, et des données sur les activités des agents.

Les données sur les activités des agents comprennent les informations suivantes : l’indice de l’activité, l’heure de début de l’activité, l’heure de fin de l’activité et l’identification de l’agent. La durée des activités est calculée en soustrayant l’heure de début de l’activité de l’heure de fin de l’activité.

Mais ces données sur les activités des angents ne concernent pas ce présent travail. Nous nous concentrons surtout sur les données des appels entrants.

Le jeu de données des d’appels qui contient les informations suivantes : l’heure d’arrivée de l’appel, l’heure à laquelle l’agent commence à traiter l’appel, l’heure de départ, le type de service demandé par l’appelant, l’identité de l’agent qui traite l’appel, etc.

Les colonnes qui nous interessent le plus sont:
- date_received: date et heure d’arrivée de l’appel;
- queue_name: l’ID du type de service demandé par l’appelant;
- angent_number: l’ID de l’agent qui traite l’appel
- answered: date et heure à laquelle l’appel a été pris(NULL sinon);
- hangup: date et heure à laquelle l’appel a pris fin;
- etc.

Le mécanisme de routage fonctionne comme suit. Lorsqu’un client appelle, il interagit avec l’IVR (unité de réponse vocale interactive) en utilisant son clavier pour choisir le type d’appel. S’il y a un agent disponible ayant les compétences nécessaires pour traiter ce type d’appel, le client (l’appel) est alors dirigé vers l’agent le plus longtemps inactif parmi les agents disponibles; sinon, il attend dans une file d’attente invisible. Les appels de cette file d’attente sont traités dans l’ordre FCFS (premier arrivé, premier servi).

## Que cherchons nous à faire

Des études réalisées sur les centres d’appels (les systémes de service en général) ont montré que le fait d’informer les clients sur leur temps d’attente dès leur arrivée permet d’augmenter la satisfaction des clients sur la qualité de service perçu, et réduit considérablement le nombre d’abandons. Ceci combiné avec la proposition de rappel si le temps d’attente estimé est très long réduit davantage les abandons et augmente la satisfaction des clients.

Dans ce travail, nous voulons tester l’efficacité de certains prédicteurs présenté dans Thiongane et al. (2023) “A New Delay History Predictor for Multi-skill Call Center”. en utilisant les données du VANAD pour l’année 2014. Nous allons utiliser les prédicteurs LES, Avg-LES, AvgC-LES et WAvgC-LES et ANN (Artificial Neural Network). LES, Avg- LES, AvgC-LES et WAvgC-LES sont des prédicteurs qui utilisent l’historique des attentes des clients passés pour prédire les temps d’attente des nouveaux clients alors que ANN est un préedicteur qui utilise l’apprentissage machine.

Pour prédire le temps d’attente W d’un nouveau client de type T, le prédicteur ANN peut utiliser toutes les informations dont pensons avoir une influence sur le temps d’attente d’un client. Bien sûr il faut que ces informations soient observables au niveau système à l’arrivée d’un client. Nous pouvons considérer un ensemble d’informations sur chaque client qui arrive sur le call center. Toutes ces informations forment un vecteur x qui décrit l’état du système à l’arrivée d’un nouveau client. En d’autres termes, à l’arrivée d’un nouveau client, on observe l’état du système x qui est passé en entrée à la fonction F pour le calcule du temps d’attente prédit pour ce client (F(x) représente alors la prédiction du temps d’attente W). 

Nous souhaitons trouver la fonction F qui minimise les erreurs de prédictions.

Dans les données toutes les informations qui décrivent le vecteur x ne sont pas observées. Le premier chalenge est de recouvrer ces données manquantes. 
Pour ce faire, nous allons faire un replay des journées du centre d’appels (simulation par retraçage). Nous allons utiliser le package simevents de la librairie SSJ. A la fin de la simulation, nous allons avoir un jeu de donn ées D = {z1,··· ,zn} avec zi = (xi,wi) où xi ≡ “état du système” et wi ≡ “temps d’attente” du client i. Nous allons utiliser 80% des données pour apprendre la fonction de prédiction F avec des méthodes d’apprentissage machine et mesurer les performances de ce dernier avec les 20% des données restantes.
