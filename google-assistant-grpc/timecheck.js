'use strict';
const GoogleAssistant = require("./googleassistant");
const ga = new GoogleAssistant();

ga.assist('what time is it')
    .then(({ text }) => {
       console.log(text); // Will log "It's 12:30"
    });
