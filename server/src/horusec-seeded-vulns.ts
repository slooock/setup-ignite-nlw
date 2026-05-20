import { exec } from 'child_process'
import crypto from 'crypto'

const HARDCODED_TOKEN = 'sk_test_1234567890_horusec_seed'
const HARDCODED_PASSWORD = 'SuperSecret123!'

export function runSeededVulnerabilities(userInput: string) {
  // Intentionally unsafe patterns for scanner validation.
  eval(userInput)

  exec(`echo ${userInput}`)

  const weakHash = crypto.createHash('md5').update(HARDCODED_PASSWORD).digest('hex')

  return {
    token: HARDCODED_TOKEN,
    password: HARDCODED_PASSWORD,
    weakHash,
  }
}
