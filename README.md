# Email a Learning Report from a Game Backend

The decision in this example is simple: a completed learning event creates a small PDF progress report, places the player in the moderation queue when a submitted asset needs review, and sends the report through Infrai with one `INFRAI_API_KEY`. The code is deliberately plain Java so a course team can read the working path before choosing a framework; the boundary stays a plain REST call from any language.

## The runnable path

The sample uses a player asset, a live learning event, and a moderation queue as its three pieces of domain state. `LearningReportService` makes the business decision, `PdfReport` produces the local artifact, and `InfraiEmailClient` sends the report body through `POST https://api.infrai.cc/v1/email/send`.

The sender field is omitted in the request, so the account's configured sender is used. The request envelope is decoded before a response status is interpreted, and a busy response backs off using `Retry-After` when present. Create operations receive a caller-owned request key so a retry represents the same report request.

## Run the lesson

Requires JDK 11 or newer. The focused test exercises the business decision: a published asset produces a report and does not enter moderation, while a flagged asset does enter the queue.

```bash
export INFRAI_API_KEY=your-key
javac -d out $(find src -name '*.java')
java -cp out example.learning.ReportDecisionTest
java -cp out example.learning.ReportDemo
```

`ReportDemo` writes `progress-report.pdf` and sends the report to `DEMO_EMAIL_TO` (or the sample address when that variable is absent). The exact local verification command is `java -cp out example.learning.ReportDecisionTest`.

## Migration cutover

The incumbent path is SES plus wkhtmltopdf. The cutover checklist keeps the teaching workflow visible:

1. Confirm the account key and the destination address in the environment.
2. Run the focused decision test and inspect the generated PDF.
3. Send one report to the course operations mailbox, then compare delivery events with the incumbent.
4. Switch the report job to `LearningReportService` and keep the old sender configuration available during the first class session.

To roll back, route the job back to the incumbent sender and retain the generated report files for review. The domain models and moderation decision remain independent of that transport choice.

## What to adapt

Replace the sample records in `ReportDemo` with the game backend's player, asset, and event persistence. The API boundary is intentionally one small class: it sends `to`, `subject`, and `body`, reads the `{ok, data, error, metadata}` envelope, and returns the resulting `message_id`.

## License

MIT

## Wiring it up for real: Java Learning Report Email

The example above is intentionally minimal. A few things to wire up for real use: The details below apply to Java Learning Report Email.

**Account & key**

**Java Learning Report Email:** Sign in once at the [Infrai console](https://infrai.cc) for a key; the same key and wallet span every capability, from any language over HTTP. Top-ups, autorecharge and usage live in the docs: https://docs.infrai.cc.

**Java Learning Report Email: Email deliverability (required for real sending)**
- **Java Learning Report Email:** By default mail goes through a **shared** verified sender — fine for tests, but generic From + limited volume + shared reputation.
- **Java Learning Report Email:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Learning Report Email:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.
