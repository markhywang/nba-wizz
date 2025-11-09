
# NBA Wizz
CSC207 Final Project

## Team Members
- Ibraheem Hussain
- Andrej Prekajski
- Mark Wang
- Parsa Hammati
- Yaohui Huang
- Wilson Liang

## Domain
Sports Analytics platform focused on NBA player and team insights. Users can look up, compare, and analyze player and team data, with features like player lookup, filters, team comparisons, performance graphs, and AI-generated insights/stat predictions.

## Features & User Stories
- **Player Search:** Search for NBA players to view career stats and performance graphs.
- **Filtering:** Filter players by team, position, and season range using dropdowns and sliders.
- **Comparison:** Compare two or more players/teams side by side.
- **Favourites:** Bookmark/favourite players/teams for quick access; toggle to unfavourite.
- **Sorting:** Sort player stats (e.g., points per game) to identify top performers.
- **AI Insights:** Generate AI summaries about player/team performance.
- **AI Predictions:** Get AI-based predictions for player/team stats.

## Use Cases
### 1. Searching for Players (Ibraheem Hussain)
- Enter player name (with autofill), select seasons and stats.
- Results shown in table and graphs (JavaFX).
- Error handling for invalid names, seasons, or missing data.

### 2. Filtering Players (Andrej Prekajski)
- Filter by team, position, season; badges show active filters.
- Paginate large results; clear filters option.
- Error handling for unavailable filters or data issues.

### 3. Compare Players/Teams (Yaohui Huang)
- Select 2-5 players/teams and time range.
- Side-by-side table comparison; scroll bar for large data.
- Handles missing/comparable data gracefully.

### 4 & 8. Favourite/Unfavourite (Parsa Hemmati)
- Toggle favourite/bookmark on player/team profile or list.
- Filter by favourites; unfavourite by toggling again.
- Message if no favourites exist.

### 5. Sorting Player Stats (Wilson Liang)
- Sort by stat columns (PPG, APG, RPG, FG%, etc.).
- Arrow indicator for sort order; toggle ascending/descending.
- Handles missing/non-numeric stats.

### 6 & 7. AI Insights & Predictions (Mark Wang)
- LLM backend generates summaries and predictions for selected player/team.
- Handles insufficient data, computation errors, and LLM failures.

## Core Entities
- **Player:** ID, name, team, position, age, height, weight, career stats, favourites count
- **Team:** ID, name, city, roster, wins/losses, conference, team stats
- **SeasonStats:** Year, points/assists/rebounds per game, FG%, games played, minutes, 3P%, player
- **AIInsight:** ID, entity type/name, summary, prediction, timestamp, confidence score
- **Filter:** ID, team, position, min/max points, season, sort field/order

## API & Libraries
- **Ollama4j:** Java API for running open-source LLMs locally (e.g., Mistral models) for AI insights/predictions.

## Datasets
- [Historical NBA Data & Player Box Scores](https://www.kaggle.com/datasets/eoinamoore/historical-nba-data-and-player-box-scores/)
- [NBA Players Data](https://www.kaggle.com/datasets/justinas/nba-players-data)
- [Basketball Dataset](https://www.kaggle.com/datasets/wyattowalsh/basketball)

## Notes
- Error handling via popups, banners, or text.
- Paginate/filter large datasets.
- Control user input for AI queries.
- Test API and libraries for reliability.


