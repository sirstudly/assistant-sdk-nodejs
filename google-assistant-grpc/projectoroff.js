'use strict';
const GoogleAssistant = require("./googleassistant");
const ga = new GoogleAssistant();

ga.assist('lights out')
    .then(({ text }) => {
       console.log(text);
    });

