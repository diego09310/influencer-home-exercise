# Comments
It's not 100% working (haven't tested much and see many discarded messages), but I've been with it more than 3h (between 4 and 5), so I'm leaving it like this.

When I got to the 3h, I had the NsqService, the influencer repository, a basic version of the kafka producer (without the exact string format) and was halfway in the ProcessData (deserialize done and was doing the logic that decides the kafka message).

## Things that need work:
- Add unit tests. I focused on having all the parts of the system working and integrated and didn't have time to add unit tests. They would help to know if the system is working or not.
- Refactor the logic that decides the kafka message. There is duplicated code for Twitter/Facebook messages that would need to be refactored.
- Refactor Influencer class. This code has many layers and the data is structured as it is in the `influencers` file. Also, in a real application all the data retrieving and part of the filtering would be done directly calling to elastic search.
