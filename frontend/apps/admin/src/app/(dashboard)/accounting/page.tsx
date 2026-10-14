"use client";
import { useState } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { FileText } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";

function useReport(
  type: string,
  params: Record<string, string>,
  enabled: boolean,
) {
  return useQuery({
    queryKey: ["accounting", type, params],
    queryFn: () =>
      api
        .get(`/accounting/reports/${type}`, { params })
        .then((r) => r.data.data),
    enabled,
  });
}

export default function AccountingPage() {
  const [incomeFrom, setIncomeFrom] = useState("");
  const [incomeTo, setIncomeTo] = useState("");
  const [balanceAsOf, setBalanceAsOf] = useState("");
  const [trialFrom, setTrialFrom] = useState("");
  const [trialTo, setTrialTo] = useState("");
  const [run, setRun] = useState<Record<string, boolean>>({});

  const income = useReport(
    "income-statement",
    { from: incomeFrom, to: incomeTo },
    !!run.income,
  );
  const balance = useReport(
    "balance-sheet",
    { asOf: balanceAsOf },
    !!run.balance,
  );
  const trial = useReport(
    "trial-balance",
    { from: trialFrom, to: trialTo },
    !!run.trial,
  );

  const ReportTable = ({ data }: { data: unknown }) => {
    if (!data) return null;
    const entries = Object.entries(data as Record<string, unknown>);
    return (
      <div className="space-y-2 text-sm">
        {entries.map(([key, value]) => (
          <div
            key={key}
            className="flex justify-between py-1.5 border-b last:border-0"
          >
            <span className="text-gray-600 capitalize">
              {key.replace(/([A-Z])/g, " $1").trim()}
            </span>
            <span className="font-semibold">
              {typeof value === "number"
                ? `$${value.toFixed(2)}`
                : String(value)}
            </span>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Accounting &amp; Reports</h1>

      <Tabs defaultValue="income">
        <TabsList>
          <TabsTrigger value="income">Income Statement</TabsTrigger>
          <TabsTrigger value="balance">Balance Sheet</TabsTrigger>
          <TabsTrigger value="trial">Trial Balance</TabsTrigger>
        </TabsList>

        <TabsContent value="income" className="mt-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-base flex items-center gap-2">
                <FileText className="w-4 h-4" />
                Income Statement
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-4 items-end flex-wrap">
                <div>
                  <Label>From</Label>
                  <Input
                    type="date"
                    value={incomeFrom}
                    onChange={(e) => setIncomeFrom(e.target.value)}
                    className="w-36"
                  />
                </div>
                <div>
                  <Label>To</Label>
                  <Input
                    type="date"
                    value={incomeTo}
                    onChange={(e) => setIncomeTo(e.target.value)}
                    className="w-36"
                  />
                </div>
                <Button
                  onClick={() => setRun((r) => ({ ...r, income: true }))}
                  disabled={!incomeFrom || !incomeTo}
                  className="bg-indigo-600 hover:bg-indigo-700"
                >
                  Generate
                </Button>
              </div>
              <Separator />
              {income.isLoading ? (
                <Skeleton className="h-32" />
              ) : income.data ? (
                <ReportTable data={income.data} />
              ) : (
                <p className="text-gray-400 text-sm text-center py-6">
                  Select a date range and click Generate
                </p>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="balance" className="mt-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-base flex items-center gap-2">
                <FileText className="w-4 h-4" />
                Balance Sheet
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-4 items-end">
                <div>
                  <Label>As of Date</Label>
                  <Input
                    type="date"
                    value={balanceAsOf}
                    onChange={(e) => setBalanceAsOf(e.target.value)}
                    className="w-36"
                  />
                </div>
                <Button
                  onClick={() => setRun((r) => ({ ...r, balance: true }))}
                  disabled={!balanceAsOf}
                  className="bg-indigo-600 hover:bg-indigo-700"
                >
                  Generate
                </Button>
              </div>
              <Separator />
              {balance.isLoading ? (
                <Skeleton className="h-32" />
              ) : balance.data ? (
                <ReportTable data={balance.data} />
              ) : (
                <p className="text-gray-400 text-sm text-center py-6">
                  Select a date and click Generate
                </p>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="trial" className="mt-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-base flex items-center gap-2">
                <FileText className="w-4 h-4" />
                Trial Balance
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-4 items-end flex-wrap">
                <div>
                  <Label>From</Label>
                  <Input
                    type="date"
                    value={trialFrom}
                    onChange={(e) => setTrialFrom(e.target.value)}
                    className="w-36"
                  />
                </div>
                <div>
                  <Label>To</Label>
                  <Input
                    type="date"
                    value={trialTo}
                    onChange={(e) => setTrialTo(e.target.value)}
                    className="w-36"
                  />
                </div>
                <Button
                  onClick={() => setRun((r) => ({ ...r, trial: true }))}
                  disabled={!trialFrom || !trialTo}
                  className="bg-indigo-600 hover:bg-indigo-700"
                >
                  Generate
                </Button>
              </div>
              <Separator />
              {trial.isLoading ? (
                <Skeleton className="h-32" />
              ) : trial.data ? (
                <ReportTable data={trial.data} />
              ) : (
                <p className="text-gray-400 text-sm text-center py-6">
                  Select a date range and click Generate
                </p>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
