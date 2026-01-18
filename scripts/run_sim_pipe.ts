/*
 * Usage:
 *   BASE_URL=http://localhost:8080 PIPE_DEF_CODE=sim_pipe ts-node scripts/run_sim_pipe.ts
 *   or: npx ts-node scripts/run_sim_pipe.ts
 */

type CreatePipeResponse = {
  success?: boolean;
  data?: {
    id?: number;
  };
  msg?: string;
};

const baseUrl = process.env.BASE_URL || "http://localhost:8080";
const pipeDefCode = process.env.PIPE_DEF_CODE || "sim_pipe";

const requestBody = {
  pipeDefCode,
  pipeContext: {
    taskId: 1,
    processId: 1,
    taskCode: "sim_task",
    cityCode: "11000",
    cityName: "test",
    ucid: "0",
    extData: {
      testKey: "haha, i am tester",
    },
    pipeData: {
      testInputKey: "testInputVal",
      testKey: "testInputVal",
    },
  },
  triggerTime: null,
};

async function main() {
  const createResp = await fetch(`${baseUrl}/pipes`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(requestBody),
  });

  if (!createResp.ok) {
    throw new Error(`create pipe failed: ${createResp.status} ${createResp.statusText}`);
  }

  const createJson = (await createResp.json()) as CreatePipeResponse;
  const pipeId = createJson?.data?.id;

  if (!pipeId) {
    throw new Error(`pipeId missing in response: ${JSON.stringify(createJson)}`);
  }

  const startResp = await fetch(`${baseUrl}/pipes/${pipeId}/start-immediately`, {
    method: "POST",
  });

  if (!startResp.ok) {
    throw new Error(`start pipe failed: ${startResp.status} ${startResp.statusText}`);
  }

  const startJson = await startResp.json();
  console.log("pipe created:", pipeId);
  console.log("start response:", startJson);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
