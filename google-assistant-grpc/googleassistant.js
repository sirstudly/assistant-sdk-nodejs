/**
 * Copyright 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';
const path = require('path');
//const grpc = require('grpc');
const grpc = require('@grpc/grpc-js');
var protoLoader = require('@grpc/proto-loader');
const protoFiles = require('google-proto-files');
const resolve = require('resolve');
const GoogleAuth = require('google-auth-library');

const PROTO_ROOT_DIR = protoFiles('..');

/*
const embedded_assistant_pb = grpc.load({
    root: PROTO_ROOT_DIR,
    file: path.relative(PROTO_ROOT_DIR, protoFiles.embeddedAssistant.v1alpha2)
}).google.assistant.embedded.v1alpha2;

*/

const deviceCredentials = require(`./devicecredentials.json`);

const CREDENTIALS = {
    client_id: deviceCredentials.client_id,
    client_secret: deviceCredentials.client_secret,
    refresh_token: deviceCredentials.refresh_token,
    type: "authorized_user"
};

var packageDefinition = protoLoader.loadSync(
    PROTO_ROOT_DIR + '/' + path.relative(PROTO_ROOT_DIR, protoFiles.embeddedAssistant.v1alpha2),
    {keepCase: true,
        longs: String,
        enums: String,
        defaults: true,
        oneofs: true,
        includeDirs: [
            PROTO_ROOT_DIR
        ],
    });
const embedded_assistant_pb = grpc.loadPackageDefinition(packageDefinition).google.assistant.embedded.v1alpha2;


//console.log( embedded_assistant_pb.AssistConfig.type ); process.exit();

class GoogleAssistant {
    constructor() {
        GoogleAssistant.prototype.endpoint_ = "embeddedassistant.googleapis.com";
        this.client = this.createClient_(CREDENTIALS);
        this.locale = "en-US";
        this.deviceModelId = 'default';
        this.deviceInstanceId = 'default';
    }
    createClient_(credentials) {
        const sslCreds = grpc.credentials.createSsl();
        // https://github.com/google/google-auth-library-nodejs/blob/master/ts/lib/auth/refreshclient.ts
        const auth = new GoogleAuth.GoogleAuth();
        const refresh = new GoogleAuth.UserRefreshClient();
        refresh.fromJSON(credentials, function (res) { });
        const callCreds = grpc.credentials.createFromGoogleCredential(refresh);
        const combinedCreds = grpc.credentials.combineChannelCredentials(sslCreds, callCreds);
        const client = new embedded_assistant_pb.EmbeddedAssistant(this.endpoint_, combinedCreds);
        return client;
    }

    assist(input) {
        /* This is the old code from using the grpc library (as opposed to @grpc/proto-loader and @grpc/grpc-js
         *
        const config = new embedded_assistant_pb.AssistConfig();

        config.setTextQuery(input);

        config.setAudioOutConfig(new embedded_assistant_pb.AudioOutConfig());
        config.getAudioOutConfig().setEncoding(1);
        config.getAudioOutConfig().setSampleRateHertz(16000);
        config.getAudioOutConfig().setVolumePercentage(100);

        config.setDialogStateIn(new embedded_assistant_pb.DialogStateIn());
        config.getDialogStateIn().setLanguageCode(this.locale);

        config.setDeviceConfig(new embedded_assistant_pb.DeviceConfig());
        config.getDeviceConfig().setDeviceId(this.deviceInstanceId);
        config.getDeviceConfig().setDeviceModelId(this.deviceModelId);

        const request = new embedded_assistant_pb.AssistRequest();
        request.setConfig(config);
        delete request.audio_in;
         */

        const request =
            { config:
                {
                    text_query: input,
                    audio_out_config: {
                        encoding: "LINEAR16",
                        sample_rate_hertz: 16000,
                        volume_percentage: 100
                    },
                    dialog_state_in: {
                        language_code: this.locale
                    },
                    device_config: {
                        device_id: this.deviceInstanceId,
                        device_model_id: this.deviceModelId
                    }
                }
            }

        const conversation = this.client.assist();
        return new Promise((resolve, reject) => {
            let response = {};
            conversation.on('data', (data) => {
                if (data.device_action) {
                    response.deviceAction = JSON.parse(data.device_action.device_request_json);
                } else if (data.dialog_state_out !== null && data.dialog_state_out.supplemental_display_text) {
                    response.text = data.dialog_state_out.supplemental_display_text;
                }
            });
            conversation.on('end', (error) => {
                // Response ended, resolve with the whole response.
                resolve(response);
            });
            conversation.on('error', (error) => {
                reject(error);
            });
            conversation.write(request);
            conversation.end();
        });
    }
}

module.exports = GoogleAssistant;
