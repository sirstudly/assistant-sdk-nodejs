'use strict';
const GoogleAssistant = require("./googleassistant");
const ga = new GoogleAssistant();

ga.assist('turn on projector')
.then(({ text }) => {
  console.log(text);
});
