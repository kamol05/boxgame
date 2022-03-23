var ws = null;
var curCon = $("#initCon");
var rId = 1;
var requestHandlers = new Map();
var timeout;
var timeoutPassed;
var timeoutTimer;

function openWebSocket(token) {
  $("#loginCon").hide(); 
  curCon = $("#initCon");
  curCon.show();
  ws = new WebSocket("ws://localhost:8080/websocket?token=" + token);
  ws.onopen = function() {
    console.log('Web socket opened');
  }
  ws.onclose = function() {
    console.log('Web socket closed');
    $("#loginCon").show();
    curCon.hide();
  }
  ws.onmessage = function(msg) {
    console.log("<< " + msg.data);
    var m = JSON.parse(msg.data);
    if (m.type === 'state') {
      updateState(m);
    } else if (m.type === 'response') {
      var h = requestHandlers.get(m.requestId);
      requestHandlers.delete(m.requestId);
      if (m.error) {
        if (h.fail) {
          h.fail(m.errorMessage);
        }
      } else if (h.success) {
        h.success(m.response);
      }
    }
  }
}

function updateState(s) {
  curCon.hide();
  if (s.state=='init') {
    curCon = $("#initCon");
    curCon.show();
    $('.waitingCount').text(s.waitingCount);
    if (timeoutTimer) {
      clearTimeout(timeoutTimer);
      timeoutTimer = null;
    }
  } else if (s.state==='wait') {
    curCon = $("#waitCon");
    curCon.show();
    $('.waitingCount').text(s.waitingCount);
    if (timeoutTimer) {
      clearTimeout(timeoutTimer);
      timeoutTimer = null;
    }
  } else if (s.state==='game') {
    if (!s.game.roundCompleted) {
      curCon = $("#roundActiveCon");
      curCon.show();      
      $("#block").val(s.game.mine.block);
      $("#kick").val(s.game.mine.kick);
    } else {
      curCon = $("#roundResultCon");
      curCon.show();      
      $("#mineBlock").val(s.game.mine.block);
      $("#mineKick").val(s.game.mine.kick);
      $("#enemyBlock").val(s.game.enemy.block);
      $("#enemyKick").val(s.game.enemy.kick);

      if (!s.game.completed) {
        $("#roundWinnerCon").show();
        $("#gameWinnerCon").hide();
        if (s.game.mine.hit == s.game.enemy.hit) {
          $(".roundWinner").text('Draw');
        } else if (s.game.mine.hit) {
          $(".roundWinner").text(s.game.mine.username);
        } else if (s.game.enemy.hit) {
          $(".roundWinner").text(s.game.enemy.username);
        }        
      } else {
        $("#roundWinnerCon").hide();
        $("#gameWinnerCon").show();
        if (s.game.mine.winner == s.game.enemy.winner) {
          $(".gameWinner").text('Draw');
        } else if (s.game.mine.winner) {
          $(".gameWinner").text(s.game.mine.username);
        } else if (s.game.enemy.winner) {
          $(".gameWinner").text(s.game.enemy.username);
        }
      }
    }
    $('.mineNickname').text(s.game.mine.username);
    $('.enemyNickname').text(s.game.enemy.username);
    $('.mineScore').text(s.game.mine.score);
    $('.enemyScore').text(s.game.enemy.score);
    $('.roundNumber').text(s.game.round);  
    
    timeout = s.game.timeout;
    timeoutPassed = s.game.timeoutPassed;
    updateTimeout();
  }
  
}

function updateTimeout() {
  if (timeoutTimer) {
    clearTimeout(timeoutTimer);
    timeoutTimer = null;
  }
  var diff = timeout - timeoutPassed;
  if (diff < 0) {
    diff = 0;
  }
  console.log(timeout + " - " + diff);
  var progress = Math.round((timeout - diff) * 100.0 / timeout);
    
  $('.progress-bar').attr('aria-valuenow', progress);
  $('.progress-bar').css("width", progress + "%");
  timeoutTimer = setTimeout(function() {
    timeoutPassed += 1000;
    updateTimeout();
  }, 1000);
}

function sendWsRequest(req, success, fail) {
  req.id = rId++;
  requestHandlers.set(req.id, {
    "request": req,
    "success": success,
    "fail": fail
  });
  // console.log(requestHandlers.values())
  console.log(">> " + JSON.stringify(req));
  ws.send(JSON.stringify(req));
}

function onJoinGame() {
  sendWsRequest({"action": "join"}, function() {}, function(errorMessage) {
    alert(errorMessage);
  });
}

function onUndoJoinGame() {
  sendWsRequest({"action": "undo_join"}, function() {}, function(errorMessage) {
    alert(errorMessage);
  });
}

function onBlockChange() {
  sendWsRequest({"action": "game_set_block", "block": $("#block").val()}, function() {}, function(errorMessage) {
    alert(errorMessage);
  });
  
}

function onKickChange() {
  sendWsRequest({"action": "game_set_kick", "kick": $("#kick").val()}, function() {}, function(errorMessage) {
    alert(errorMessage);
  });
  
}

function onSignIn() {
  var username = $("#username").val();
  var pass = $("#password").val();
  if (username && pass) {
      $.ajax({
            type: 'post',
            url: '/login',
            data: JSON.stringify({"username": username, "password": pass}),
            contentType: "application/json; charset=utf-8",
            traditional: true,
            success: function (data) {
                openWebSocket(data.token);
            },
            
        }).fail(function(response) {
           alert(response.responseJSON.errorMessage);
        });
  }
}