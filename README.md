![AWS CodeBuild Build Badge](https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoicGJKRmVaUGJGcEZvaiszQkxhc3U4L1duRTgySU8vbHpNUDlTVDVpZlN6OUxnQVl3RWlLRmxQN2JQYjltSUxjQ3RiQThNdGJST042SkZJYVpITUYwTzJFPSIsIml2UGFyYW1ldGVyU3BlYyI6IjZ2N1lRZjE0bi94ek9mbU4iLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)

# NHLP3
Serverless **N**HL **P**lay-by-**P**lay **P**rocessor *(hence NHLP3)* built using [AWS Lambda](https://aws.amazon.com/) and NHL's public APIs *(more information can be found at [Drew Hynes's](https://gitlab.com/dword4) [NHL Stats API Documentation project](https://gitlab.com/dword4/nhlapi/blob/master/stats-api.md))*.

![NHLP3 Diagram](https://pbs.twimg.com/media/ECWDcoCUEAAUnoC?format=png)

# Scheduler Function
The purpose of this function is to be called once daily and retrieve all of the NHL games for the specified day. It will
then set up the play-by-play processor to process each game at the start of each game.
