import "./QuestionCard.css";

import { useEffect, useMemo, useState } from "react";

function highlightText(text, term) {
  if (!term || !text) return text;
  const regex = new RegExp(`(${term.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, "gi");
  return text.split(regex).map((part, i) =>
    regex.test(part) ? <span key={i} style={{ background: "yellow" }}>{part}</span> : part
  );
}
import {
  editAnswer,
  editQuestion,
  removeQuestion,
  reportAnswer,
  reportQuestion,
  submitAnswer,
  upvoteAnswer,
  upvoteQuestion,
  removeUpvoteAnswer,
  removeUpvoteQuestion,
} from "../api/qaApi";
import ReportModal from "../../report/modals/ReportModal";

const isWithinEditWindow = (createdAt) => {
  if (!createdAt) return false;
  const created = new Date(createdAt);
  if (Number.isNaN(created.getTime())) return false;
  const diffMs = Date.now() - created.getTime();
  return diffMs >= 0 && diffMs <= 15 * 60 * 1000;
};

const formatDateTime = (value) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toLocaleString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
};

export default function QuestionCard({
  question,
  onRefresh,
  currentUserName,
  isLoggedIn,
  onRequireLogin,
  onToast,
  searchTerm,
}) {
  if (!question) return null;

  const canEdit = useMemo(
    () => isWithinEditWindow(question.createdAt),
    [question.createdAt]
  );
  const isAuthor = Boolean(
    currentUserName && question.authorName && currentUserName === question.authorName
  );
  const hasUserAnswered = Boolean(
    currentUserName && question.answers?.some((answer) => answer.authorName === currentUserName)
  );

  // State declarations
  const [isEditing, setIsEditing] = useState(false);
  const [editText, setEditText] = useState(question.text ?? "");
  const [editError, setEditError] = useState("");
  const [isAnswering, setIsAnswering] = useState(false);
  const [answerText, setAnswerText] = useState("");
  const [answerError, setAnswerError] = useState("");
  const [editingAnswerId, setEditingAnswerId] = useState(null);
  const [answerEditText, setAnswerEditText] = useState("");
  const [answerEditError, setAnswerEditError] = useState("");
  const [questionHasUpvoted, setQuestionHasUpvoted] = useState(Boolean(question.hasUpvoted));
  const [questionUpvoteCount, setQuestionUpvoteCount] = useState(Number(question.upvoteCount ?? 0));
  const [answerUpvotes, setAnswerUpvotes] = useState(() => {
    const upvotes = {};
    question.answers?.forEach((a) => {
      upvotes[a.id] = {
        count: Number(a.upvoteCount ?? 0),
        hasUpvoted: Boolean(a.hasUpvoted),
      };
    });
    return upvotes;
  });
  const [reportOpen, setReportOpen] = useState(false);
  const [reportTarget, setReportTarget] = useState(null);
  const [isHidden, setIsHidden] = useState(false);
  const [hiddenAnswerIds, setHiddenAnswerIds] = useState(new Set());
  const askedAt = formatDateTime(question.createdAt);

  // Show edit/answer buttons only if within edit window or not answered
  const showEdit = isAuthor && canEdit;
  const showAnswer = isLoggedIn && !isAuthor;

  useEffect(() => {
    setEditText(question.text ?? "");
    setQuestionHasUpvoted(Boolean(question.hasUpvoted));
    setQuestionUpvoteCount(Number(question.upvoteCount ?? 0));
    setAnswerUpvotes(() => {
      const upvotes = {};
      question.answers?.forEach((a) => {
        upvotes[a.id] = {
          count: Number(a.upvoteCount ?? 0),
          hasUpvoted: Boolean(a.hasUpvoted),
        };
      });
      return upvotes;
    });
  }, [question]);

  const handleEditSubmit = async (event) => {
    event.preventDefault();
    const trimmed = editText.trim();
    if (trimmed.length < 2) {
      setEditError("Question must be at least 2 characters.");
      return;
    }
    setEditError("");
    const success = await editQuestion(question.id, trimmed);
    if (success) {
      setIsEditing(false);
      onRefresh?.();
      onToast?.("Question updated.", "success");
    } else {
      setEditError("Unable to update question.");
      onToast?.("Unable to update question.", "error");
    }
  };

  const handleAnswerSubmit = async (event) => {
    event.preventDefault();
    const trimmed = answerText.trim();
    if (trimmed.length < 2) {
      setAnswerError("Answer must be at least 2 characters.");
      return;
    }
    setAnswerError("");
    const success = await submitAnswer(question.id, trimmed);
    if (success) {
      setAnswerText("");
      setIsAnswering(false);
      onRefresh?.();
      onToast?.("Answer submitted.", "success");
    } else {
      setAnswerError("Unable to submit answer.");
      onToast?.("Unable to submit answer.", "error");
    }
  };

  const handleAnswerEditSubmit = async (event) => {
    event.preventDefault();
    const trimmed = answerEditText.trim();
    if (trimmed.length < 2) {
      setAnswerEditError("Answer must be at least 2 characters.");
      return;
    }

    setAnswerEditError("");
    const success = await editAnswer(editingAnswerId, trimmed);
    if (success) {
      setEditingAnswerId(null);
      setAnswerEditText("");
      onRefresh?.();
      onToast?.("Answer updated.", "success");
    } else {
      setAnswerEditError("Unable to update answer.");
      onToast?.("Unable to update answer.", "error");
    }
  };

  const handleQuestionUpvote = async () => {
    if (!isLoggedIn) {
      onRequireLogin?.();
      return;
    }
    if (isAuthor) return;

    const isUpvoted = questionHasUpvoted;
    const success = isUpvoted
      ? await removeUpvoteQuestion(question.id)
      : await upvoteQuestion(question.id);
    if (success) {
      setQuestionHasUpvoted(!isUpvoted);
      setQuestionUpvoteCount((count) => Math.max(0, count + (isUpvoted ? -1 : 1)));
      onToast?.(isUpvoted ? "Question upvote removed." : "Question upvoted.", "success");
    } else {
      onToast?.("Unable to update question upvote.", "error");
    }
  };

  const handleAnswerUpvote = async (answerId, isAnswerAuthor) => {
    if (!isLoggedIn) {
      onRequireLogin?.();
      return;
    }
    if (isAnswerAuthor) return;

    const isUpvoted = Boolean(answerUpvotes[answerId]?.hasUpvoted);
    const success = isUpvoted
      ? await removeUpvoteAnswer(answerId)
      : await upvoteAnswer(answerId);
    if (success) {
      setAnswerUpvotes((prev) => ({
        ...prev,
        [answerId]: {
          count: Math.max(0, (prev[answerId]?.count ?? 0) + (isUpvoted ? -1 : 1)),
          hasUpvoted: !isUpvoted,
        },
      }));
      onToast?.(isUpvoted ? "Answer upvote removed." : "Answer upvoted.", "success");
    } else {
      onToast?.("Unable to update answer upvote.", "error");
    }
  };

  const openReportModal = (target) => {
    if (!isLoggedIn) {
      onRequireLogin?.();
      return;
    }
    setReportTarget(target);
    setReportOpen(true);
  };

  const handleReportSubmit = async ({ reason }) => {
    if (!reportTarget) return;

    const success = reportTarget.type === "question"
      ? await reportQuestion(reportTarget.id, reason)
      : await reportAnswer(reportTarget.id, reason);

    if (success) {
      if (reportTarget.type === "question") {
        setIsHidden(true);
      } else {
        setHiddenAnswerIds((prev) => {
          const next = new Set(prev);
          next.add(reportTarget.id);
          return next;
        });
      }
      setReportOpen(false);
      setReportTarget(null);
      onRefresh?.();
      onToast?.("Report submitted. Thanks for the feedback.", "success");
    } else {
      onToast?.("Unable to report right now.", "error");
    }
  };

  const handleQuestionDelete = async () => {
    if (!isLoggedIn) {
      onRequireLogin?.();
      return;
    }
    if (!isAuthor) return;

    const confirmed = window.confirm("Delete this question? This can't be undone.");
    if (!confirmed) return;

    const success = await removeQuestion(question.id);
    if (success) {
      onRefresh?.();
      onToast?.("Question deleted.", "success");
    } else {
      onToast?.("Unable to delete question.", "error");
    }
  };

  if (isHidden) return null;

  return (
    <div className="question-card">
      <ReportModal
        isOpen={reportOpen}
        onClose={() => {
          setReportOpen(false);
          setReportTarget(null);
        }}
        title={reportTarget?.type === "answer" ? "Report answer" : "Report question"}
        subtitle={reportTarget?.subtitle}
        placeholder={
          reportTarget?.type === "answer"
            ? "Tell us why you are reporting this answer..."
            : "Tell us why you are reporting this question..."
        }
        onSubmit={handleReportSubmit}
      />
      <div className="question-card__header">
        <div className="question-text">{highlightText(question.text, searchTerm)}</div>
        <div className="question-header-actions">
          {question.answers?.length ? (
            <span className="question-status">Answered</span>
          ) : null}
          {!isAuthor ? (
            <button
              type="button"
              className="question-action-btn"
              onClick={handleQuestionUpvote}
            >
              {questionHasUpvoted ? "Undo" : "Upvote"}
            </button>
          ) : null}
          {!isAuthor ? (
            <button
              type="button"
              className="question-action-btn"
              onClick={() =>
                openReportModal({
                  type: "question",
                  id: question.id,
                  subtitle: question.authorName
                    ? `Asked by ${question.authorName}`
                    : "",
                })
              }
            >
              Report
            </button>
          ) : null}
          <span className="question-upvotes">{questionUpvoteCount} upvotes</span>
        </div>
      </div>
      {showEdit || showAnswer || isAuthor ? (
        <div className="question-actions">
          {showEdit ? (
            <button
              type="button"
              className="question-action-btn"
              onClick={() => {
                setIsEditing((value) => !value);
                setEditError("");
              }}
            >
              {isEditing ? "Cancel edit" : "Edit"}
            </button>
          ) : null}
          {isAuthor ? (
            <button
              type="button"
              className="question-action-btn question-action-danger"
              onClick={handleQuestionDelete}
            >
              Delete
            </button>
          ) : null}
          {showAnswer ? (
            hasUserAnswered ? (
              <span className="question-note">You answered this question!</span>
            ) : (
              <button
                type="button"
                className="question-action-btn"
                onClick={() => {
                  if (!isLoggedIn) {
                    onRequireLogin?.();
                    return;
                  }
                  setIsAnswering((value) => !value);
                  setAnswerError("");
                }}
              >
                {isAnswering ? "Cancel answer" : "Answer"}
              </button>
            )
          ) : null}
        </div>
      ) : null}
      <div className="question-meta">
        {isAuthor ? (
          <span>Asked by you</span>
        ) : question.authorName ? (
          <span>Asked by {question.authorName}</span>
        ) : null}
        {askedAt ? <span>{askedAt}</span> : null}
      </div>

      {isEditing ? (
        <form className="question-edit-form" onSubmit={handleEditSubmit}>
          <textarea
            className="question-edit-textarea"
            value={editText}
            onChange={(event) => setEditText(event.target.value)}
            rows={3}
            maxLength={800}
          />
          {editError ? <p className="question-error">{editError}</p> : null}
          <button type="submit" className="question-submit-btn">
            Save changes
          </button>
        </form>
      ) : null}

      {isAnswering ? (
        <form className="question-answer-form" onSubmit={handleAnswerSubmit}>
          <textarea
            className="question-answer-textarea"
            value={answerText}
            onChange={(event) => setAnswerText(event.target.value)}
            rows={3}
            maxLength={800}
          />
          {answerError ? <p className="question-error">{answerError}</p> : null}
          <button type="submit" className="question-submit-btn">
            Submit answer
          </button>
        </form>
      ) : null}

      {question.answers?.length ? (
        <div className="question-answers">
          {question.answers
            .filter((answer) => !hiddenAnswerIds.has(answer.id))
            .map((answer) => {
            const isAnswerAuthor = Boolean(
              currentUserName && answer.authorName && currentUserName === answer.authorName
            );
            const isEditingAnswer = editingAnswerId === answer.id;
            const answerUpvoteData = answerUpvotes[answer.id] ?? {
              count: Number(answer.upvoteCount ?? 0),
              hasUpvoted: Boolean(answer.hasUpvoted),
            };
            const answeredAt = formatDateTime(answer.createdAt);

            return (
              <div key={answer.id} className="question-answer">
                <div className="question-answer__header">
                  <p className="answer-text">{highlightText(answer.text, searchTerm)}</p>
                  <div className="question-answer-actions">
                    {isAnswerAuthor ? (
                      <button
                        type="button"
                        className="question-action-btn"
                        onClick={() => {
                          if (!isLoggedIn) {
                            onRequireLogin?.();
                            return;
                          }
                          setEditingAnswerId((value) =>
                            value === answer.id ? null : answer.id
                          );
                          setAnswerEditText(answer.text ?? "");
                          setAnswerEditError("");
                        }}
                      >
                        {isEditingAnswer ? "Cancel edit" : "Edit answer"}
                      </button>
                    ) : null}
                    {!isAnswerAuthor ? (
                      <button
                        type="button"
                        className="question-action-btn"
                        onClick={() => handleAnswerUpvote(answer.id, isAnswerAuthor)}
                      >
                        {answerUpvoteData.hasUpvoted ? "Undo" : "Upvote"}
                      </button>
                    ) : null}
                    {!isAnswerAuthor ? (
                      <button
                        type="button"
                        className="question-action-btn"
                        onClick={() =>
                          openReportModal({
                            type: "answer",
                            id: answer.id,
                            subtitle: answer.authorName
                              ? `Answered by ${answer.authorName}`
                              : "",
                          })
                        }
                      >
                        Report
                      </button>
                    ) : null}
                    <span className="answer-upvotes">{answerUpvoteData.count} upvotes</span>
                  </div>
                </div>
                <div className="answer-meta">
                  {answer.authorName ? (
                    <span>Answered by {answer.authorName}</span>
                  ) : null}
                  {answeredAt ? <span>{answeredAt}</span> : null}
                </div>
                {isEditingAnswer ? (
                  <form className="question-answer-form" onSubmit={handleAnswerEditSubmit}>
                    <textarea
                      className="question-answer-textarea"
                      value={answerEditText}
                      onChange={(event) => setAnswerEditText(event.target.value)}
                      rows={3}
                      maxLength={800}
                    />
                    {answerEditError ? (
                      <p className="question-error">{answerEditError}</p>
                    ) : null}
                    <button type="submit" className="question-submit-btn">
                      Save answer
                    </button>
                  </form>
                ) : null}
              </div>
            );
          })}
        </div>
      ) : null}
    </div>
  );
}
