
var url = "https://zhang.nz/tatai/getHighScores.php";
var params = "gamemode=";
defaultGameMode = "Numbers";

function getData(gameMode) {
  console.log('test');
  var http = new XMLHttpRequest();
  http.open("POST", url, true);
  //Send the proper header information along with the request
  http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  http.onreadystatechange = function() {//Call a function when the state changes.
      if(http.readyState == 4 && http.status == 200) {
          //alert(http.responseText);
          //document.getElementById('highscores').innerHTML = http.responseText;
          var jsonObj = JSON.parse(http.responseText);
          processData(jsonObj)
      }
  }
  http.send(params + gameMode);
}

getData(defaultGameMode);

function getDateString(jsonScoreObj) {
    var unixTime = jsonScoreObj.datetime;
    var date = new Date(unixTime * 1000);
    return date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear();
}

function getUsernameString(jsonScoreObj) {
  return jsonScoreObj.username;
}

function getScoreString(jsonScoreObj) {
  return jsonScoreObj.score;
}

function createRow(jsonScoreObj) {
  var newRow = document.createElement('tr');

  var placeCell = document.createElement('td');
  //placeCell.className = "w3-quarter";
  placeCell.innerHTML = jsonScoreObj.place;
  newRow.appendChild(placeCell);

  var usernameCell = document.createElement('td');
  usernameCell.innerHTML = getUsernameString(jsonScoreObj);
  newRow.appendChild(usernameCell);

  var scoreCell = document.createElement('td');
  scoreCell.innerHTML = getScoreString(jsonScoreObj);
  newRow.appendChild(scoreCell);

  var dateCell = document.createElement('td');
  dateCell.innerHTML = getDateString(jsonScoreObj);
  newRow.appendChild(dateCell);

  return newRow;
}

function processData(jsonObj) {
  document.getElementById('highscores').innerHTML = "";
  for (i = 0; i < jsonObj.scores.length && i < 10; i++) {
    var row = createRow(jsonObj.scores[i])
    document.getElementById('highscores').appendChild(row);
  }
}
