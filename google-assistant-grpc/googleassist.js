'use strict';
const GoogleAssistant = require("./googleassistant");
const ga = new GoogleAssistant();

if(process.argv.length > 2) {
        ga.assist(process.argv[2])
                .then(({ text }) => {
                  console.log(text);
                });
}
else {
        console.log("No parameter specified.");
}

