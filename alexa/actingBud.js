//    skeleton code for the VUI (lines 0-593)
'use strict';


const doc = require('dynamodb-doc');

const dynamo = new doc.DynamoDB();

var database = {};
var event1;
var context1;

exports.handler = function (event, context, callback) {
	event1 = event;
	context1 = context;
	//Christopher's table name for our scripts is Script. Each entry in the Script database is a JSON script
	dynamo.scan({TableName: "Script"}, onScan);

};

function onScan(err, data) {
	if (err) {
    	console.error("Unable to scan the table. Error JSON:", JSON.stringify(err, null, 2));
	} else {
    	data.Items.forEach(function(script) {
        	database[script.Script_Name] = JSON.parse(script.Data);

     	})
	}
	try {
    	if (event1.session.new) {
        	onSessionStarted({requestId: event1.request.requestId}, event1.session);
    	}

    	if (event1.request.type === "LaunchRequest") {
        	onLaunch(event1.request,
            	event1.session,
            	function callback(sessionAttributes, speechletResponse) {
                	context1.succeed(buildResponse(sessionAttributes, speechletResponse));
            	});
    	} else if (event1.request.type === "IntentRequest") {
        	onIntent(event1.request,
            	event1.session,
            	function callback(sessionAttributes, speechletResponse) {
                	context1.succeed(buildResponse(sessionAttributes, speechletResponse));
            	});
    	} else if (event1.request.type === "SessionEndedRequest") {
        	onSessionEnded(event1.request, event1.session);
        	context1.succeed();
    	}
	} catch (e) {
    	context1.fail("Exception: " + e);
	}
}

function onSessionStarted(sessionStartedRequest, session) {
	// add any session init logic here
}

/**
 * Called when the user invokes the skill without specifying what they want.
 */
function onLaunch(launchRequest, session, callback) {
	getWelcomeResponse(callback);
}

function onSessionEnded(sessionEndedRequest, session) {

}

function onIntent(intentRequest, session, callback) {
	var intent = intentRequest.intent;
	var intentName = intentRequest.intent.name;
	// executed if the user either says 'start practicing' or 'done line'
	if (intentName == "MainIntent" || intentName == "FinishedLineIntent" || intentName == "ContinueIntent") {
    	practicingDialog(intent, session, callback);
	} else if (intentName == "RepeatIntent" || intentName == "ResumeIntent" || intentName == "AMAZON.NoIntent" && session.attributes.askedToQuit === true) {
    	handleCueRequest(intent, session, callback);
	} else if (intentName == "CurrentLineIntent") {
    	handleCurrentLineRequest(intent, session, callback);
	} else if (intentName == "PauseIntent" ) {
    	handlePauseRequest(intent, session, callback);
	} else if (intentName == "JumpForwardIntent") {
    	handleJumpForwardRequest(intent, session, callback);
	} else if (intentName == "StartOverIntent") {
    	handleStartOverRequest(intent, session, callback);
	} else if (intentName == "WhatCanISayIntent") {
    	whatCanISayResponse(intent, session, callback);
	} else if (intentName == "StopIntent") {
    	handleConfirmQuitIntent(intent, session, callback);
	} else if (intentName == "AMAZON.YesIntent" && session.attributes.askedToQuit === true) {
    	handleQuitIntent(intent,session,callback);
	} else {
    	handleUnknownRequest(intent, session, callback);
	}
}

function getWelcomeResponse(callback) {
	//get scriptName from database based off what user selected in GUI
	var scriptName = database["status"]["scriptName"];

	//get Act name from database based off what user selected in GUI
	//get them from the status
	var actNum = database["status"]["actNum"];

	//get scene name from database based off what user selected in GUI
	var sceneNum = database["status"]["sceneNum"];

	//get role from Database based off what user selected in GUI
	var roleName = database["status"]["roleName"].toLowerCase();

	//list of start and end indexes, inclusive of the scene
	
	var sceneLineNum = database[scriptName]["acts"][actNum+","+sceneNum];
	//return [0,94]

	//the index of the first line of the scene
	var sceneStartLineNum = sceneLineNum[0];
	//0

	//the index of the last line of the scene
	var sceneEndLineNum = sceneLineNum[1];
	//94

	// the line we expect the user to be on
	var practiceLineNum = sceneStartLineNum;


	var speechOutput = "Welcome to Acting Bud! You've selected to practice " + scriptName + " scene " + sceneNum + " act " + actNum 
	+ " for " + roleName + ". When you are ready to begin, please say 'start practicing'. To select another play, say 'quit'.";

	var reprompt = "Please say 'start practicing'.";

	var header = "Acting Bud";

	var shouldEndSession = false;

	var sessionAttributes = {
    	speechOutput: speechOutput,
    	repromptText : reprompt,
    	scriptName : scriptName,
    	actNum :actNum,
    	sceneNum : sceneNum,
    	roleName : roleName,
    	sceneStartLineNum : sceneStartLineNum,
    	sceneEndLineNum : sceneEndLineNum,
    	practiceLineNum: practiceLineNum
    
	};

	callback(sessionAttributes, buildSpeechletResponse(header, speechOutput, reprompt, shouldEndSession));

}

function practicingDialog(intent, session, callback) {
   	var intentName = intent.name;
	var scriptName = session.attributes.scriptName;
	var actNum = session.attributes.actNum;
	var sceneNum = session.attributes.sceneNum;
	var roleName = session.attributes.roleName;
	var sceneStartLineNum = session.attributes.sceneStartLineNum;
	var sceneEndLineNum = session.attributes. sceneEndLineNum;
	var practiceLineNum = session.attributes.practiceLineNum;

    var currentLine;
    var character;
    if (practiceLineNum <= sceneEndLineNum && practiceLineNum >= sceneStartLineNum) {
        currentLine = database[scriptName]["lines"][practiceLineNum];
    	character = currentLine["character"];
    }
    	//generic case
    if (intentName != "MainIntent" && intentName != "JumpForwardIntent" && intentName != "StartOverIntent"  && intentName != "ContinueIntent"){
       		 //always the user read line
       	practiceLineNum+=1;
        if (practiceLineNum <= sceneEndLineNum && practiceLineNum >= sceneStartLineNum) {
        	currentLine = database[scriptName]["lines"][practiceLineNum];
        	character = currentLine["character"];
    	}
    }
    var speechOutput = "";
    var numLines = 0;
    if (practiceLineNum <= sceneEndLineNum && practiceLineNum >= sceneStartLineNum) {
    	while (character != roleName) {
        	speechOutput += " " + character + " says, " + currentLine["line"];
        	practiceLineNum += 1;
        	numLines += 1;
        	if (practiceLineNum > sceneEndLineNum) {
        	   speechOutput += " You have finished practicing " + scriptName + " act "+actNum+" scene " + sceneNum + " in the role of " + roleName +
    	                " Please say 'quit' to return to the GUI. Thank you.";
    	       break;
        	}
        	currentLine = database[scriptName]["lines"][practiceLineNum];
        	character = currentLine["character"];
        	if (numLines > 5 && character != roleName) {
        	    speechOutput += " Say 'continue lines' to continue the script."
        	    break;
        	}

    	}
   	 
	} else {
    	speechOutput = "You have finished practicing " + scriptName + " act "+actNum+" scene " + sceneNum + " in the role of " + roleName +
    	   " Please say 'quit' to return to the GUI. Thank you.";
	}

	var reprompt = speechOutput;

	var header = "Practicing Dialog";

	var shouldEndSession = false;

	var sessionAttributes = {
    	speechOutput: speechOutput,
    	repromptText : reprompt,
    	scriptName : scriptName,
    	actNum :actNum,
    	sceneNum : sceneNum,
    	roleName : roleName,
    	sceneStartLineNum : sceneStartLineNum,
    	sceneEndLineNum : sceneEndLineNum,
    	practiceLineNum: practiceLineNum
	};

	callback(sessionAttributes, buildSpeechletResponseWithoutCard(speechOutput, reprompt, shouldEndSession));

}

function handleCueRequest(intent, session, callback) {
	var scriptName = session.attributes.scriptName;
	var actNum = session.attributes.actNum;
	var sceneNum = session.attributes.sceneNum;
	var roleName = session.attributes.roleName;
	var sceneStartLineNum = session.attributes.sceneStartLineNum;
	var sceneEndLineNum = session.attributes. sceneEndLineNum;
	var practiceLineNum = session.attributes.practiceLineNum;
    
	var header = "Cue Request";
    
	var shouldEndSession = false;
    
	//if we are at the first line, there is no practice line to cue
	var speechOutput = "";
	var reprompt = "";
    if (sceneStartLineNum > practiceLineNum -1){
   	 	speechOutput = "There are no lines before the current line.";
	 	reprompt = speechOutput;
	} else {
		var currentLine = database[scriptName]["lines"][practiceLineNum -1];
		var character = currentLine["character"];
		var line = currentLine["line"];
		speechOutput  = character + " says, " + line;
		reprompt = speechOutput;
	}
		
  	var sessionAttributes = {
      	scriptName : scriptName,
      	actNum : actNum,
      	sceneNum : sceneNum,
      	roleName : roleName,
      	sceneStartLineNum : sceneStartLineNum,
      	sceneEndLineNum : sceneEndLineNum,
      	practiceLineNum: practiceLineNum,
  	};

  	callback(sessionAttributes, buildSpeechletResponse(header, speechOutput, reprompt, shouldEndSession));
    }

function handleCurrentLineRequest(intent, session, callback) {
	var scriptName = session.attributes.scriptName;
	var actNum = session.attributes.actNum;
	var sceneNum = session.attributes.sceneNum;
	var roleName = session.attributes.roleName;
	var sceneStartLineNum = session.attributes.sceneStartLineNum;
	var sceneEndLineNum = session.attributes. sceneEndLineNum;
	var practiceLineNum = session.attributes.practiceLineNum;
	var currentLine = database[scriptName]["lines"][practiceLineNum];
	var  character = currentLine["character"];

	var line = currentLine["line"];

	var speechOutput = character+ " says, " + line;

	var reprompt = speechOutput;

	var header = "Handle Current Line";

	var shouldEndSession = false;

	// expect user to repeat line
	var sessionAttributes = {
    	speechOutput : speechOutput,
    	repromptText : reprompt,
    	scriptName : scriptName,
    	actNum : actNum,
    	sceneNum : sceneNum,
    	roleName : roleName,
    	sceneStartLineNum : sceneStartLineNum,
    	sceneEndLineNum : sceneEndLineNum,
    	practiceLineNum: practiceLineNum,
    	};

	callback(sessionAttributes, buildSpeechletResponse(header, speechOutput, reprompt, shouldEndSession));

}

function handlePauseRequest(intent, session, callback) {
	var scriptName = session.attributes.scriptName;
	var actNum = session.attributes.actNum;
	var sceneNum = session.attributes.sceneNum;
	var roleName = session.attributes.roleName;
	var sceneStartLineNum = session.attributes.sceneStartLineNum;
	var sceneEndLineNum = session.attributes. sceneEndLineNum;
	var practiceLineNum = session.attributes.practiceLineNum;
	
	var speechOutput = "Pausing practicing lines. When you want to resume, please say ‘resume practicing.’";

	var reprompt = speechOutput;

	var header = "Handle Pausing Lines";

	var shouldEndSession = false;

	var sessionAttributes = {
    	speechOutput : speechOutput,
    	repromptText : reprompt,
    	scriptName : scriptName,
    	actNum : actNum,
    	sceneNum : sceneNum,
    	roleName : roleName,
    	sceneStartLineNum : sceneStartLineNum,
    	sceneEndLineNum : sceneEndLineNum,
    	practiceLineNum: practiceLineNum,
    	};

	callback(sessionAttributes, buildSpeechletResponse(header, speechOutput, reprompt, shouldEndSession));

}

function handleJumpForwardRequest(intent, session, callback) {
	var scriptName = session.attributes.scriptName;
	var actNum = session.attributes.actNum;
	var sceneNum = session.attributes.sceneNum;
	var roleName = session.attributes.roleName;
	var sceneStartLineNum = session.attributes.sceneStartLineNum;
	var sceneEndLineNum = session.attributes. sceneEndLineNum;
	var practiceLineNum = session.attributes.practiceLineNum;
	
	var lineNumber = parseInt(intent.slots.Answer.value);
	// 99
	var header = "Handle Jump Forward Request";

	var shouldEndSession = false;

    session.attributes.practiceLineNum = lineNumber;

	practicingDialog(intent, session, callback);

}

function handleStartOverRequest(intent, session, callback) {
	var scriptName = session.attributes.scriptName;
	var actNum = session.attributes.actNum;
	var sceneNum = session.attributes.sceneNum;
	var roleName = session.attributes.roleName;
	var sceneStartLineNum = session.attributes.sceneStartLineNum;
	var sceneEndLineNum = session.attributes.sceneEndLineNum;
	var practiceLineNum = sceneStartLineNum;
	
    var header = "Handle Start Over Request";

	var shouldEndSession = false;

    session.attributes.practiceLineNum = session.attributes.sceneStartLineNum;

	practicingDialog(intent, session, callback);
	
}


function whatCanISayResponse(intent, session, callback) {
	var scriptName = session.attributes.scriptName;
	var actNum = session.attributes.actNum;
	var sceneNum = session.attributes.sceneNum;
	var roleName = session.attributes.roleName;
	var sceneStartLineNum = session.attributes.sceneStartLineNum;
	var sceneEndLineNum = session.attributes. sceneEndLineNum;
	var practiceLineNum = session.attributes.practiceLineNum;
	
	var speechOutput = "You can say 'begin practicing', 'finished line', 'stop practicing lines'," +
	" 'cue me', 'what’s my line', 'pause practicing', 'continue practicing', 'start over', or 'jump to x line.'";

	var reprompt = speechOutput;

	var header = "What Can I Say";

	var shouldEndSession = false;


	var sessionAttributes = {
    	speechOutput : speechOutput,
    	repromptText : reprompt,
    	scriptName : scriptName,
    	actNum : actNum,
    	sceneNum : sceneNum,
    	roleName : roleName,
    	sceneStartLineNum : sceneStartLineNum,
    	sceneEndLineNum : sceneEndLineNum,
    	practiceLineNum: practiceLineNum,
    	};

	callback(sessionAttributes, buildSpeechletResponse(header, speechOutput, reprompt, shouldEndSession));

}


function handleUnknownRequest(intent, session, callback) {
	var speechOutput = "Sorry I don't recognize that request. Please say 'what can I say'.";
	var reprompt = speechOutput;
	var shouldEndSession = false;
	var header = "unknown Request";

	var sessionAttributes = {
    	speechOutput: speechOutput,
    	repromptText: reprompt
	};
	callback(sessionAttributes, buildSpeechletResponse(header, speechOutput, reprompt, shouldEndSession));

}

function handleConfirmQuitIntent(intent, session, callback) {
	var speechOutput = "Are you sure you want to quit, Acting Bud. To quit, say 'yes'. To resume, say 'no'.";
	var reprompt = speechOutput;
	var shouldEndSession = false;
	var header = "Confirm Quit Request";

    session.attributes.speechOutput = speechOutput;
    session.attributes.rempromptText = reprompt;
    session.attributes.askedToQuit = true;

	callback(session.attributes, buildSpeechletResponse(header, speechOutput, reprompt, shouldEndSession));

}

function handleQuitIntent(intent, session, callback) {
	handleFinishSessionRequest(intent, session, callback);
}

function handleFinishSessionRequest(intent, session, callback) {
	// End the session with a "Good bye!" if the user wants to quit the game
	callback(session.attributes,
    	buildSpeechletResponseWithoutCard("Goodbye! Thank you for using Acting Bud!", "", true));
}

// ------- Helper functions to build responses for Alexa -------


function buildSpeechletResponse(title, output, repromptText, shouldEndSession) {
	return {
    	outputSpeech: {
        	type: "PlainText",
        	text: output
    	},
    	card: {
        	type: "Simple",
        	title: title,
        	content: output
    	},
    	reprompt: {
        	outputSpeech: {
            	type: "PlainText",
            	text: repromptText
        	}
    	},
    	shouldEndSession: shouldEndSession
	};
}

function buildSpeechletResponseWithoutCard(output, repromptText, shouldEndSession) {
	return {
    	outputSpeech: {
        	type: "PlainText",
        	text: output
    	},
    	reprompt: {
        	outputSpeech: {
            	type: "PlainText",
            	text: repromptText
        	}
    	},
    	shouldEndSession: shouldEndSession
	};
}

function buildResponse(sessionAttributes, speechletResponse) {
	return {
    	version: "1.0",
    	sessionAttributes: sessionAttributes,
    	response: speechletResponse
	};
}


