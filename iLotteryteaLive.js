// Copyright (C) 2022 ilotterytea
// 
// This file is part of iLotteryteaLive.
// 
// iLotteryteaLive is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// iLotteryteaLive is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with iLotteryteaLive.  If not, see <http://www.gnu.org/licenses/>.

const { existsSync, mkdirSync, readFileSync, writeFileSync } = require("fs");
const { ApolloClient } = require("./src/apollo/ApolloClient");
const ApolloLogger = require("./src/apollo/utils/ApolloLogger");
const { TwitchGQL } = require("./src/apollo/utils/HelixTwitch");
const { WebClient } = require("./src/web/WebClient");

require("dotenv").config({path: "./bot.env"});

/**
 * Bot initializer.
 */
async function Initialize() {
    async function DirectoryCheck() {
        if (!(existsSync("storage"))) {
            mkdirSync("storage");
            mkdirSync("storage/logs");
            mkdirSync("storage/pubdata");
            mkdirSync("storage/msgdata");
            writeFileSync("storage/storage.json", JSON.stringify({
                version: "v1",
                join: {
                    asanonymous: [],
                    asclient: []
                },
                prefix: "!",
                emotes: {
                },
                roles: {
                    authority: [],
                    feelingspecial: [],
                    permabanned: []
                },
                stats: {
                    chat_lines: {},
                    executed_commands: {},
                    tests: {}
                }
            }, null, 2), {encoding: "utf-8"});
        }
    }

    ApolloLogger("--- Bot is starting! Checking the directories...", "log", true);
    await DirectoryCheck();
    ApolloLogger("Initializing the bot components...", "log", true);

    let storage = JSON.parse(readFileSync("storage/storage.json", {encoding: "utf-8"}));
    const GQL = new TwitchGQL(process.env.TTV_CLIENT, process.env.TTV_TOKEN);

    var channels = await GQL.getNamesByIds(storage.join.asclient);

    const apolloClient = new ApolloClient({
        username: process.env.TTV_LOGIN,
        password: process.env.TTV_PASSWORD,
        channelsToJoin: channels
    }, storage, GQL);

    const webClient = new WebClient(apolloClient, 12906, __dirname);

    // Web client in development stage...
    //webClient.create();
    apolloClient.create();
}


Initialize();

process.on("unhandledRejection", async (reason, promise) => {
    console.log(reason);
});

process.on("uncaughtException", async (err) => {
    console.log(err);
    return process.exit(1);
});