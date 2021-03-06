USE ristogo;

INSERT INTO users VALUES (1, 'simone', '77333912ff3f04664178edac9eed20978fdf86a776095c06fcc9afc79d6934be');
INSERT INTO users VALUES (2, 'luca', '2154ca7d4d5955314f99d846e75f10b9fb6cc8ec6560127197319d005499374f');
INSERT INTO users VALUES (3, 'gino', 'c178b9927c956b6b7d08aff3617bee4bde18b84ba346b5a8788c47f832e6ead3');
INSERT INTO users VALUES (4, 'carlotta', '7a25faacaf8ea321215bc6c116ed40b971dee103c23bf573f8603a369a90c689');
INSERT INTO users VALUES (5, 'gianluca', 'ec27c5dcb1aba241730da0ab0e695920ac927de0399226d226b82b0e8b01103f');
INSERT INTO users VALUES (6, 'lorenzo21', 'd1346bca5cc285441eb3ae13293996b12323553e313fafc3f4efb5831fb09136');
INSERT INTO users VALUES (7, 'mark', '771795917a4700741d27b968cf422b0985ba45e1d45937413135f579cb6aff02');
INSERT INTO users VALUES (8, 'lorenzo1', 'aa2e074b46f16207061796ce8375593f09ac41ca1b194858fae262b595dfa984');
INSERT INTO users VALUES (9, 'gianni', '76baff6552dd1fb5905c2ccfec76bf548961225d15832194af40a86fa027ab7f');
INSERT INTO users VALUES (10, 'gabriele97', '29ee85bfd8675bd48268bd4460c30cc3b1b939213080b7714ddb988680d5745c');

INSERT INTO restaurants VALUES
(1, 4, 'The Pirate Cabin', 'ITALIAN', 'HIGH', 'Donoratico', 'Del Cipresso, 37', 'The Pirate Cabin is the dream of two brothers: Alberto and Carlotta Olmi, who in 1970 decided to invest in the beauty and potential of the Etruscan Coast, a place of unique and authentic charm, lulled by the gentle countryside of Bolgheri and the waves of Tirreno. Since then the structure on the beach has grown together with the Olmi family, without losing the charm of the origins, acquiring a splendid panoramic terrace, rustling raffia umbrellas, cozy tents and becoming one of the most renowned restaurants on the Coast, marked by the most prestigious Gourmand Guides', 15, 'DINNER'),
(2, 2, 'Pizza to Luca', 'PIZZA', 'LOW', 'Pisa', 'Milano, 14', 'The real Neapolitan pizza prepared according to the tradition of the recipe with real Mozzarella di Bufala Campana DOP. You will find a cheerful atmosphere and the splendid sympathy of the managers and all the staff. The atmosphere is familiar and welcoming and offers some details of the Neapolitan city. Our ingredients are imported directly from Campania and are selected with great care: the buffalo mozzarella from Campania that gives that fresh flavor to doc pizzas, type 00 soft wheat flour, yeast of brewer, peeled and fresh tomatoes, the extra virgin olive oil and basil. All this is cooked in a wood oven', 30, 'DINNER'),
(3, 5, 'Tavern of Cason', 'ITALIAN', 'MIDDLE', 'Cascina', 'Torino, 78', 'The values that gave life to our business are the passion for our work, the strong corporate culture of a family business and the constant search for customer satisfaction. You will find Tuscan dishes and home-produced wines served between colorful chairs and a family atmosphere', 10, 'LUNCH'),
(4, 7, 'HomeHam', 'STEAKHOUSE', 'MIDDLE', 'Pisa', 'Diotisalvi, 1', 'Selected and grilled meat cuts at the moment, many tasty and quality proposals, daily promotions and dedicated offers: HomeHam is this but also much more. We have chosen to be a restaurant where quality is not only tasted at the table but also in the services, atmosphere and technological facilities we offer you, which is why we are the restaurant that was not there', 35, 'DINNER'),
(5, 3, 'To Mexican', 'MEXICAN', 'MIDDLE', 'Pisa', 'Giacomo Leopardi, 3', 'Explosive cocktail of colors and pleasure for the eyes and the palate (the local majolica toilets are the jewel in the crown), the new headquarters in Via Giacomo Leopardi 3 awaits all Mexican cuisine lovers: 30 years of history and experience are just a great start to amaze again and start again', 12, 'DINNER'),
(6, 9, 'Japanese Zen', 'JAPANESE', 'LUXURY', 'Cascina', 'Zizzo, 66', 'For over 15 years Zen at Cascina is the reference for all those who wish to enjoy authentic Japanese cuisine in a unique atmosphere. The long experience of the chefs, the freshest raw materials and selected by the best of local markets, for an assortment of culinary proposals that have made Zen successful.', 20, 'BOTH');

INSERT INTO reservations VALUES (1, 1, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), 'DINNER', 5);
INSERT INTO reservations VALUES (2, 2, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), 'DINNER', 6);
INSERT INTO reservations VALUES (3, 3, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), 'DINNER', 2);
INSERT INTO reservations VALUES (4, 4, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 2 DAY), 'DINNER', 3);
INSERT INTO reservations VALUES (5, 4, 3, DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), 'LUNCH', 4);
INSERT INTO reservations VALUES (6, 5, 5, DATE_ADD(CURRENT_DATE(), INTERVAL 5 DAY), 'DINNER', 6);
INSERT INTO reservations VALUES (7, 7, 6, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 'DINNER', 4);
INSERT INTO reservations VALUES (8, 10, 6, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 'LUNCH', 3);
